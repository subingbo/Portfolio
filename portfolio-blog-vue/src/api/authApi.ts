import { assertRuoYiR, httpJson, unwrapR } from './http'
import type { LoginVo } from './types/auth'

function clientId(): string {
  return (
    import.meta.env.VITE_RUOYI_CLIENT_ID?.trim() ||
    'e5cd7e4891bf95d1d19206ce24a7b32e'
  )
}

function tenantId(): string {
  return import.meta.env.VITE_RUOYI_TENANT_ID?.trim() || '000000'
}

export interface CaptchaVo {
  captchaEnabled?: boolean
  uuid?: string
  img?: string
}

export async function fetchCaptcha(): Promise<CaptchaVo> {
  const body = await httpJson<unknown>('/auth/code')
  const r = assertRuoYiR<CaptchaVo>(body)
  if (r.data === undefined || r.data === null) {
    throw new Error(r.msg || '获取验证码失败')
  }
  return r.data
}

export async function loginByPassword(params: {
  username: string
  password: string
  code?: string
  uuid?: string
}): Promise<LoginVo> {
  const payload: Record<string, string> = {
    clientId: clientId(),
    grantType: 'password',
    tenantId: tenantId(),
    username: params.username,
    password: params.password,
  }
  if (params.code != null && params.uuid != null) {
    payload.code = params.code
    payload.uuid = params.uuid
  }
  const body = await httpJson<unknown>('/auth/login', {
    method: 'POST',
    body: payload,
    encrypt: true,
  })
  const r = assertRuoYiR<LoginVo>(body)
  return unwrapR(r)
}

export async function registerUser(params: {
  username: string
  password: string
  code?: string
  uuid?: string
}): Promise<void> {
  const payload: Record<string, string> = {
    clientId: clientId(),
    grantType: 'password',
    tenantId: tenantId(),
    username: params.username,
    password: params.password,
    userType: 'sys_user',
  }
  if (params.code != null && params.uuid != null) {
    payload.code = params.code
    payload.uuid = params.uuid
  }
  const body = await httpJson<unknown>('/auth/register', {
    method: 'POST',
    body: payload,
    encrypt: true,
  })
  const r = assertRuoYiR<unknown>(body)
  unwrapR(r)
}

export async function authLogout(): Promise<void> {
  const token = localStorage.getItem('ruoyi_access_token')
  if (!token) return
  await httpJson<unknown>('/auth/logout', {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: {},
  })
}
