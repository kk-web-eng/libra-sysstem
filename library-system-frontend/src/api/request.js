import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 401) {
      sessionStorage.clear()
      window.location.href = '/login'
      return Promise.reject(new Error(res.msg))
    }
    if (res.code === 403) {
      ElMessage.error(res.msg || '没有权限')
      window.location.href = '/dashboard'
      return Promise.reject(new Error(res.msg))
    }
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return res
  },
  error => {
    ElMessage.error('网络连接异常')
    return Promise.reject(error)
  }
)

export default request
