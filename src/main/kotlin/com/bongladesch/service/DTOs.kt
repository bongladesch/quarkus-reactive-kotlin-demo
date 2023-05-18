package com.bongladesch.service

import io.vertx.core.file.AsyncFile
import java.nio.file.Path

data class FileDTO(val name: String, val mimeType: String, val path: Path)

data class AsyncFileDTO(val name: String, val mimeType: String, val file: AsyncFile)
