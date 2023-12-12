package com.example.socialvocal.modeles

class AudioFile {
    lateinit var fileName: String
    lateinit var audio: ByteArray
    constructor() {}
    constructor(username: String, audio: ByteArray) {
        this.fileName = username
        this.audio = audio
    }
}