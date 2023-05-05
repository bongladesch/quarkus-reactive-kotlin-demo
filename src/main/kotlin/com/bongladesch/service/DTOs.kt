package com.bongladesch.service

import java.io.InputStream

data class FileDTO(val name: String, val mimeType: String, val fileStream: InputStream)
