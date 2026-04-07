import CryptoJS from 'crypto-js'
import JSEncrypt from 'jsencrypt'

function pemPublicKeyFromBase64Der(b64: string): string {
  const body = b64.match(/.{1,64}/g)?.join('\n') ?? b64
  return `-----BEGIN PUBLIC KEY-----\n${body}\n-----END PUBLIC KEY-----`
}

function normalizePublicKey(input: string): string {
  const raw = input.trim()
  if (raw.includes('BEGIN PUBLIC KEY')) {
    // 兼容 .env 中使用 \\n 拼接 PEM 的写法
    return raw.replace(/\\n/g, '\n')
  }
  return pemPublicKeyFromBase64Der(raw)
}

function randomAesPassword16(): string {
  const chars =
    'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let s = ''
  for (let i = 0; i < 16; i++) {
    s += chars[Math.floor(Math.random() * chars.length)]
  }
  return s
}

/** 与 Java Base64.encode(aesPassword, UTF_8) 一致 */
function utf8ToBase64(s: string): string {
  return btoa(unescape(encodeURIComponent(s)))
}

/**
 * 与 RuoYi {@code api-decrypt} + Hutool AES/ECB/PKCS5、RSA 公钥加密流程对齐。
 */
export function encryptRuoYiRequestBody(
  plainUtf8Json: string,
  rsaPublicKeyBase64Der: string
): { cipherBody: string; encryptKeyHeader: string } {
  const aesPassword = randomAesPassword16()
  const key = CryptoJS.enc.Utf8.parse(aesPassword)
  const aesEncrypted = CryptoJS.AES.encrypt(plainUtf8Json, key, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  })
  const cipherBody = aesEncrypted.ciphertext.toString(CryptoJS.enc.Base64)

  const aesKeyBase64 = utf8ToBase64(aesPassword)
  const jsEncrypt = new JSEncrypt()
  jsEncrypt.setPublicKey(normalizePublicKey(rsaPublicKeyBase64Der))
  const encrypted = jsEncrypt.encrypt(aesKeyBase64)
  const encryptKeyHeader = encrypted ? encrypted.replace(/\s+/g, '') : ''
  if (!encryptKeyHeader) {
    throw new Error('RSA 加密失败，请检查 VITE_AUTH_RSA_PUBLIC_KEY 是否与后端 api-decrypt 公钥一致')
  }
  return { cipherBody, encryptKeyHeader }
}
