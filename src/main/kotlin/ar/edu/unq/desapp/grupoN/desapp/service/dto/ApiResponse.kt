package ar.edu.unq.desapp.grupoN.desapp.service.dto

data class ApiResponse<T>(var data: T?, var errors: Map<String,String>?) {
    constructor(data: T) : this(data, null)
}