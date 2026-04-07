/** 与后端 LoginVo 字段（JSON 蛇形）对齐 */
export interface LoginVo {
  access_token?: string
  refresh_token?: string
  expire_in?: number
  refresh_expire_in?: number
  client_id?: string
  scope?: string
  openid?: string
}
