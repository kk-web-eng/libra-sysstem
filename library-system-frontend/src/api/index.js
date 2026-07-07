import request from './request'

export function login(username, password) {
  return request.post('/login', { username, password })
}

export function logout() {
  return request.post('/logout')
}

export function getCurrentUser() {
  return request.get('/currentUser')
}

export function userRegister(data) {
  return request.post('/user/register', data)
}

export function userLogin(account, password) {
  return request.post('/user/login', { account, password })
}

export function userLogout() {
  return request.post('/user/logout')
}

export function userBorrow(bookId, data) {
  return request.post('/user/borrow/' + bookId, data)
}

export function getUserBorrows(params) {
  return request.get('/user/borrows', { params })
}

export function renewUserBorrow(id, data) {
  return request.post('/user/renew/' + id, data)
}

export function getDashboard() {
  return request.get('/dashboard')
}

export function getPublicBooks(params) {
  return request.get('/public/books', { params })
}

export function getPublicCategories() {
  return request.get('/public/categories')
}

export function getBooks(params) {
  return request.get('/books', { params })
}

export function getBookCategories() {
  return request.get('/books/categories')
}

export function addBook(data) {
  return request.post('/books', data)
}

export function updateBook(data) {
  return request.put('/books', data)
}

export function deleteBook(id) {
  return request.delete('/books/' + id)
}

export function getReaders(params) {
  return request.get('/readers', { params })
}

export function addReader(data) {
  return request.post('/readers', data)
}

export function updateReader(data) {
  return request.put('/readers', data)
}

export function deleteReader(id) {
  return request.delete('/readers/' + id)
}

export function getBorrows(params) {
  return request.get('/borrows', { params })
}

export function borrowBook(data) {
  return request.post('/borrows/borrow', data)
}

export function returnBook(id) {
  return request.post('/borrows/return/' + id)
}

export function renewBorrow(id, data) {
  return request.post('/borrows/renew/' + id, data)
}

export function getAdmins(params) {
  return request.get('/admins', { params })
}

export function addAdmin(data) {
  return request.post('/admins', data)
}

export function updateAdmin(data) {
  return request.put('/admins', data)
}

export function deleteAdmin(id) {
  return request.delete('/admins/' + id)
}
