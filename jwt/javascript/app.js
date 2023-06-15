import crypto from "crypto"

function base64(json) {
    // JSON을 문자열화
    const stringfied = JSON.stringify(json)
    // 문자열화된 JSON을 BASE64로 인코딩
    const base64Encoded = Buffer.from(stringfied).toString("base64")
    // Base64 의 Padding(= or ==)을 제거
    const paddingRemoved = base64Encoded.replaceAll("=", "")

    return paddingRemoved
}

// Header 만들기
const header = {
    alg: "HS256",
    typ: "JWT",
}

const encodedHeader = base64(header)
// eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9

// Payload 만들기
const payload = {
    email: "mgkim.developer@gmail.com",
    name : "mgyo",
    isAdmin: true,
}

const encodedPayload = base64(payload)
// eyJlbWFpbCI6Im1na2ltLmRldmVsb3BlckBnbWFpbC5jb20iLCJuYW1lIjoibWd5byIsImlzQWRtaW4iOnRydWV9

// Signature 만들기
const signature = crypto
    .createHmac("sha256", "secret_key")
    .update('${encodedHeader}.${encodedPayload}')
    .digest("base64")
    .replaceAll("=", "")  
// IkqsW-Nuz8aeIdl4xtV9zA5KVzHkl22pHbmJCh7-I_0

const jwt = '${encodedHeader}.${encodedPayload}.${signature}'
// eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1na2ltLmRldmVsb3BlckBnbWFpbC5jb20iLCJuYW1lIjoibWd5byIsImlzQWRtaW4iOnRydWV9.IkqsW-Nuz8aeIdl4xtV9zA5KVzHkl22pHbmJCh7-I_0

// https://jwt.io/