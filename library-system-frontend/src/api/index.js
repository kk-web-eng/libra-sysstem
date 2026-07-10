import request from './request'

// 本文件集中管理所有后端接口。
// 关键词：API、HTTP 方法、请求参数。
// 页面只调用这里的函数，避免在多个页面重复编写 axios 地址。

// -------------------- 管理员登录 --------------------

/** 管理员登录：成功后，后端会把管理员信息和角色写入 Session。 */
export function login(username, password) {
  return request.post('/login', { username, password })
}

/** 管理员退出：使后端 Session 失效。 */
export function logout() {
  return request.post('/logout')
}

/** 查询当前管理员：刷新页面后可以恢复账号、姓名和角色。 */
export function getCurrentUser() {
  return request.get('/currentUser')
}

// -------------------- 读者账号与自助借阅 --------------------

/** 注册读者账号。data 中主要包含 account 和 password。 */
export function userRegister(data) {
  return request.post('/user/register', data)
}

/** 读者登录。成功后页面会保存 readerId 和 readerName。 */
export function userLogin(account, password) {
  return request.post('/user/login', { account, password })
}

/** 读者退出登录。 */
export function userLogout() {
  return request.post('/user/logout')
}

/** bookId 是图书主键，data.dueDate 是读者选择的应还日期。 */
export function userBorrow(bookId, data) {
  return request.post('/user/borrow/' + bookId, data)
}

/** 查询当前登录读者自己的借阅记录。 */
export function getUserBorrows(params) {
  return request.get('/user/borrows', { params })
}

/** 读者续借：id 是记录主键，data.dueDate 是新的应还日期。 */
export function renewUserBorrow(id, data) {
  return request.post('/user/renew/' + id, data)
}

// -------------------- 数据总览与公共馆藏 --------------------

/** 获取后台首页统计数据。 */
export function getDashboard() {
  return request.get('/dashboard')
}

/** 公共馆藏分页查询：不登录也可以搜索图书。 */
export function getPublicBooks(params) {
  return request.get('/public/books', { params })
}

/** 获取公共馆藏中的分类列表。 */
export function getPublicCategories() {
  return request.get('/public/categories')
}

// -------------------- 图书管理 --------------------

/** 后台分页查询图书。 */
export function getBooks(params) {
  return request.get('/books', { params })
}

/** 后台获取图书分类。 */
export function getBookCategories() {
  return request.get('/books/categories')
}

/** 新增图书。 */
export function addBook(data) {
  return request.post('/books', data)
}

/** 修改图书。 */
export function updateBook(data) {
  return request.put('/books', data)
}

/** 按主键删除图书。 */
export function deleteBook(id) {
  return request.delete('/books/' + id)
}

// -------------------- 读者管理 --------------------

/** 后台分页查询读者。 */
export function getReaders(params) {
  return request.get('/readers', { params })
}

/** 后台新增读者。 */
export function addReader(data) {
  return request.post('/readers', data)
}

/** 后台修改读者资料或状态。 */
export function updateReader(data) {
  return request.put('/readers', data)
}

/** 后台删除读者。 */
export function deleteReader(id) {
  return request.delete('/readers/' + id)
}

// -------------------- 借阅管理 --------------------

/** 分页查询借阅记录，可按关键词和状态筛选。 */
export function getBorrows(params) {
  return request.get('/borrows', { params })
}

/** 管理员登记借书。data 中包含读者、图书和日期信息。 */
export function borrowBook(data) {
  return request.post('/borrows/borrow', data)
}

/** 归还图书：id 是借阅记录主键。 */
export function returnBook(id) {
  return request.post('/borrows/return/' + id)
}

/** 管理员续借，data.dueDate 是新的应还日期。 */
export function renewBorrow(id, data) {
  return request.post('/borrows/renew/' + id, data)
}

// -------------------- 管理员账号管理 --------------------
// 这些接口只有系统管理员可以成功调用，后端还会再次校验角色。

/** 分页查询后台管理员。 */
export function getAdmins(params) {
  return request.get('/admins', { params })
}

/** 新增普通管理员。 */
export function addAdmin(data) {
  return request.post('/admins', data)
}

/** 修改管理员信息或启用状态。 */
export function updateAdmin(data) {
  return request.put('/admins', data)
}

/** 删除管理员账号。 */
export function deleteAdmin(id) {
  return request.delete('/admins/' + id)
}
