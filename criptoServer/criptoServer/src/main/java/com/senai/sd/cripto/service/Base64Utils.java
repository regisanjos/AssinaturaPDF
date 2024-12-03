

package com.senai.sd.cripto.service;

import java.util.Base64;

public class Base64Utils {

    // Método para converter byte[] para String em Base64
    public String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    // Método para converter String Base64 de volta para byte[]
    public byte[] fromBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }
}