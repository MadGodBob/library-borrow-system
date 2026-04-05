import http from './http'

export const loginApi = (data) => http.post('/user/login', data)
export const registerApi = (data) => http.post('/user/register', data)

export const getUserPageApi = (params) => http.get('/user/searchByPage', { params })
export const saveUserApi = (data) => http.post('/user/save', data)
export const updateUserApi = (data) => http.put('/user/update', data)
export const deleteUserApi = (id) => http.delete(`/user/${id}`)

export const getBookPageApi = (params) => http.get('/book/searchByPage', { params })
export const saveBookApi = (data) => http.post('/book/save', data)
export const updateBookApi = (data) => http.put('/book/update', data)
export const deleteBookApi = (id) => http.delete(`/book/${id}`)

export const getBorrowPageApi = (params) => http.get('/borrow/searchByPage', { params })
export const updateBorrowApi = (data) => http.put('/borrow/update', data)
export const deleteBorrowApi = (id) => http.delete(`/borrow/${id}`)

export const lendBookApi = (params) => http.get('/userLend/lendBook', { params })
export const returnBookApi = (params) => http.get('/userLend/returnBook', { params })

export const getLogPageApi = (params) => http.get('/operationLog/searchByPage', { params })

