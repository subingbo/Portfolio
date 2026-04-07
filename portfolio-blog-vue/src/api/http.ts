import axios, { AxiosError, AxiosHeaders, type AxiosRequestConfig } from 'axios'
import { getApiBase } from './config'
import { encryptRuoYiRequestBody } from './ruoyiCrypto'

export class ApiError extends Error {
  constructor(
    message: string,
    public readonly status: number,
    public readonly body?: unknown
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

declare module 'axios' {
  interface AxiosRequestConfig {
    encrypt?: boolean
  }
}

function useEncrypt(): boolean {
  return import.meta.env.VITE_AUTH_ENCRYPT !== 'false'
}

function rsaPublicKey(): string {
  const key = import.meta.env.VITE_AUTH_RSA_PUBLIC_KEY?.trim()
  if (!key) {
    throw new Error(
      '未配置 VITE_AUTH_RSA_PUBLIC_KEY。若已关闭后端 api-decrypt.enabled，请同时设置 VITE_AUTH_ENCRYPT=false'
    )
  }
  return key
}

const apiClient = axios.create({
  baseURL: getApiBase(),
  timeout: 15000,
  headers: { Accept: 'application/json' },
})

apiClient.interceptors.request.use((config) => {
  const method = (config.method ?? 'get').toUpperCase()
  const shouldEncrypt =
    config.encrypt === true &&
    useEncrypt() &&
    ['POST', 'PUT', 'PATCH'].includes(method) &&
    config.data !== undefined
  const headers = AxiosHeaders.from(config.headers)

  if (shouldEncrypt) {
    const plain =
      typeof config.data === 'string' ? config.data : JSON.stringify(config.data)
    const { cipherBody, encryptKeyHeader } = encryptRuoYiRequestBody(
      plain,
      rsaPublicKey()
    )
    config.data = cipherBody
    headers.set('encrypt-key', encryptKeyHeader)
    headers.set('Content-Type', 'application/json;charset=UTF-8')
    config.headers = headers
    return config
  }

  if (config.data !== undefined && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json;charset=UTF-8')
  }
  config.headers = headers
  return config
})

/** RuoYi 统一包装 R\<T\> */
export interface RuoYiR<T> {
  code: number
  msg: string
  data: T
}

/** 分页表格 TableDataInfo\<T\> */
export interface RuoYiTable<T> {
  code: number
  msg: string
  total: number
  rows: T[]
}

export interface HttpJsonInit {
  method?: string
  headers?: Record<string, string>
  body?: unknown
  encrypt?: boolean
  timeoutMs?: number
}

export async function httpJson<T>(
  path: string,
  init?: HttpJsonInit
): Promise<T> {
  try {
    const res = await apiClient.request<T>({
      url: path,
      method: init?.method ?? 'GET',
      headers: init?.headers,
      data: init?.body,
      encrypt: init?.encrypt,
      timeout: init?.timeoutMs,
    } as AxiosRequestConfig<T>)
    return res.data
  } catch (error) {
    if (error instanceof AxiosError) {
      const status = error.response?.status ?? 0
      throw new ApiError(
        status ? `HTTP ${status}` : error.message || '网络异常',
        status,
        error.response?.data
      )
    }
    throw error
  }
}

export function assertRuoYiR<T>(body: unknown): RuoYiR<T> {
  if (
    body &&
    typeof body === 'object' &&
    'code' in body &&
    typeof (body as RuoYiR<T>).code === 'number'
  ) {
    return body as RuoYiR<T>
  }
  throw new ApiError('响应不是 R 结构', 500, body)
}

export function assertRuoYiTable<T>(body: unknown): RuoYiTable<T> {
  if (
    body &&
    typeof body === 'object' &&
    'rows' in body &&
    Array.isArray((body as RuoYiTable<T>).rows)
  ) {
    return body as RuoYiTable<T>
  }
  throw new ApiError('响应不是 TableDataInfo 结构', 500, body)
}

export function unwrapR<T>(r: RuoYiR<T>): T {
  if (r.code === 200) return r.data
  throw new ApiError(r.msg || '业务失败', r.code, r)
}

export function unwrapTable<T>(t: RuoYiTable<T>): { total: number; rows: T[] } {
  if (t.code === 200) return { total: t.total, rows: t.rows }
  throw new ApiError(t.msg || '查询失败', t.code, t)
}
