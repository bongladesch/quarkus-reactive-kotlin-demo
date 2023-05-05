package com.bongladesch.entity

import jakarta.persistence.Cacheable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
@Cacheable
class FileMetaData {

    @Id
    lateinit var id: String

    @Column(unique = true)
    lateinit var name: String

    lateinit var mimeType: String
}
