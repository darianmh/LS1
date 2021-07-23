package com.darian.ls1.Model

data class ApiResponse<T>(var data: T, var error: String, var ok: Boolean)