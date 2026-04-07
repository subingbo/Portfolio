/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE: string
  /** 设为 false 时登录/注册明文 JSON，需同步关闭后端 api-decrypt.enabled */
  readonly VITE_AUTH_ENCRYPT: string
  /** 与 RuoYi application.yml 中 api-decrypt.publicKey（Base64）一致，用于加密 AES 密钥 */
  readonly VITE_AUTH_RSA_PUBLIC_KEY: string
  /** 默认 PC 端 client_id，对应库表 sys_client */
  readonly VITE_RUOYI_CLIENT_ID: string
  /** 租户编号，默认 000000 */
  readonly VITE_RUOYI_TENANT_ID: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<object, object, unknown>
  export default component
}
