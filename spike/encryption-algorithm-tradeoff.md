# Encryption Algorithm Tradeoff Analysis

## Executive Summary
We recommend **AES-GCM** for the FileShare System because it combines strong authenticated encryption, hardware acceleration support, and broad ecosystem compatibility—ideal for securing files in a multi-user system.

---

## Option 1: AES-GCM (Recommended ✓)

### What it is
- **Algorithm**: Advanced Encryption Standard (AES) in Galois/Counter Mode
- **Type**: Symmetric authenticated encryption (AEAD - Authenticated Encryption with Associated Data)
- **Key Size**: 128, 192, or 256 bits (256-bit recommended)
- **IV Size**: 96 bits (12 bytes, standard)
- **Authentication Tag**: 128 bits (16 bytes)

### Pros
✅ **Authenticated Encryption**: Detects tampering (integrity + confidentiality)  
✅ **Hardware Acceleration**: AES-NI on modern CPUs (Intel, AMD) → ~10-50x faster  
✅ **NIST Approved**: Federal standard, FIPS 140-2 compliant  
✅ **Industry Standard**: Used by TLS 1.3, MySQL, PostgreSQL, Cloud providers  
✅ **Single Pass**: Encrypt + authenticate in one operation  
✅ **Streaming Support**: Can encrypt/decrypt large files without loading entirely in memory  
✅ **Java Support**: Built into javax.crypto (no external libs needed)  

### Cons
❌ **IV Management Critical**: Must generate random unique IV per encryption; reuse allows forgery  
❌ **Slower on older CPUs**: Without AES-NI, slower than CBC mode  
❌ **Tag Size Fixed**: Always 128-bit authentication tag (not flexible)  

### Performance
- **Throughput** (AES-NI enabled): ~3-5 GB/sec
- **Per-file overhead**: ~28 bytes (12-byte IV + 16-byte tag)

### Use Cases
- File storage encryption ✓ (our use case)
- Database field encryption
- API communication encryption (TLS 1.3)
- Cloud storage encryption (AWS S3, Azure)

---

## Option 2: AES-CBC + HMAC (Traditional)

### What it is
- **Algorithm**: AES in Cipher Block Chaining (CBC) mode + HMAC-SHA256 for authentication
- **Type**: Symmetric encryption + separate authentication
- **Key Size**: 256 bits (AES) + 256 bits (HMAC) = 512 bits total
- **IV Size**: 128 bits (16 bytes)
- **Authentication Tag**: 256 bits (32 bytes via HMAC-SHA256)

### Pros
✅ **Well-Understood**: Proven design (used for 15+ years)  
✅ **Flexible**: Can choose different HMAC algorithms (SHA256, SHA512)  
✅ **Wide Support**: Works on all Java versions (no special hardware needed)  
✅ **Defensive**: Two separate operations reduce implementation risk  

### Cons
❌ **Two-Pass Encryption**: Encrypt first, then authenticate (slower)  
❌ **Higher Key Material**: Need 2 keys (encrypt + auth)  
❌ **Larger Overhead**: 48 bytes per file (16-byte IV + 32-byte HMAC)  
❌ **Padding Oracle Risk**: CBC mode vulnerable if padding validation leaks information (though unlikely with HMAC)  
❌ **Deprecated Trend**: Being phased out in favor of AEAD modes (TLS 1.3 removed CBC)  

### Performance
- **Throughput**: ~500 MB/sec (without AES-NI)
- **Per-file overhead**: ~48 bytes

### Use Cases
- Legacy systems
- Systems without AES-NI support
- Regulatory compliance with older standards

---

## Option 3: ChaCha20-Poly1305 (Modern Alternative)

### What it is
- **Algorithm**: ChaCha20 stream cipher + Poly1305 authenticator
- **Type**: Symmetric authenticated encryption (AEAD)
- **Key Size**: 256 bits (fixed)
- **IV Size**: 96 bits (12 bytes, nonce)
- **Authentication Tag**: 128 bits (16 bytes)

### Pros
✅ **No Hardware Dependency**: Fast on CPUs without AES-NI (mobile, ARM)  
✅ **Modern**: Newer design, battle-tested by Google/CloudFlare  
✅ **Side-Channel Resistant**: Not vulnerable to timing attacks like AES-CBC  
✅ **IETF Standard**: RFC 7539 / RFC 8439  
✅ **Single-Pass**: Like GCM, authenticate while encrypting  
✅ **Smaller Output**: 12-byte nonce vs 16-byte IV  

### Cons
❌ **Not FIPS Approved**: Can't use for federal/compliance systems  
❌ **Hardware Acceleration Limited**: No dedicated CPU instruction (slower on AES-NI systems)  
❌ **Java Support Lightweight**: javax.crypto added in Java 11 (limited versions)  
❌ **Less Industry Adoption**: Still newer than AES-GCM  
❌ **Key Rotation Complex**: 96-bit nonce space smaller than AES-GCM  

### Performance
- **Throughput** (with AES-NI): ~1-2 GB/sec (slower than AES-GCM on modern CPUs)
- **Without AES-NI**: ~300-500 MB/sec (faster than AES-CBC)
- **Per-file overhead**: ~28 bytes (12-byte nonce + 16-byte tag)

### Use Cases
- Mobile/ARM systems
- Non-compliance systems (startups, internal tools)
- TLS 1.3 alternative cipher suite
- Linux kernel encryption (dm-crypt)

---

## Comparison Table

| Criterion | AES-GCM | AES-CBC + HMAC | ChaCha20-Poly1305 |
|-----------|---------|----------------|-------------------|
| **Type** | AEAD | Encrypt + MAC | AEAD |
| **Strength** | 256-bit | 512-bit | 256-bit |
| **Authenticated?** | ✅ Yes | ✅ Yes (HMAC) | ✅ Yes |
| **Hardware Accel** | ✅ AES-NI | ❌ No | ❌ No |
| **FIPS Approved** | ✅ Yes | ✅ Yes | ❌ No |
| **Java Native** | ✅ Yes | ✅ Yes | ⚠️ Java 11+ |
| **Throughput** | ~3-5 GB/s | ~500 MB/s | ~1-2 GB/s |
| **Overhead/file** | 28 bytes | 48 bytes | 28 bytes |
| **Nonce Reuse Risk** | 🔴 Critical | 🟡 Medium | 🟡 Medium |
| **Streaming** | ✅ Excellent | ✅ Good | ✅ Excellent |
| **Complexity** | 🟢 Low | 🟡 Medium | 🟢 Low |

---

## Decision Matrix (Scoring: 1-5, higher is better)

| Factor | Weight | AES-GCM | AES-CBC+HMAC | ChaCha20 |
|--------|--------|---------|--------------|----------|
| Performance | 25% | 5 | 2 | 3 |
| Security | 30% | 5 | 4 | 5 |
| Compliance/FIPS | 20% | 5 | 5 | 1 |
| Java Support | 15% | 5 | 5 | 3 |
| Simplicity | 10% | 5 | 3 | 4 |
| **TOTAL SCORE** | **100%** | **4.8** | **3.7** | **3.5** |

---

## Final Recommendation: AES-GCM ✓

### Why?
1. **Best Performance**: 10x faster than CBC+HMAC on modern processors (AES-NI)
2. **Single Pass**: Encrypt and authenticate simultaneously
3. **Industry Standard**: TLS 1.3, AWS, Azure, Google all use it
4. **Compliance Ready**: FIPS 140-2, suitable for enterprise/government
5. **Low Overhead**: Only 28 bytes per file (vs 48 for CBC+HMAC)
6. **Java Native**: No dependencies, built-in javax.crypto
7. **Streaming**: Perfect for large file encryption

### When NOT to use AES-GCM
- If compliance requires non-NIST algorithms → use ChaCha20-Poly1305
- If you're on very old Java versions (<Java 8) → use AES-CBC+HMAC
- If you need ChaCha20 specifically for ARM/mobile performance → consider hybrid approach

### Implementation Notes
```yaml
Algorithm: AES-GCM
Key Size: 256 bits (32 bytes)
IV Generation: Random 96 bits (12 bytes) per file
Auth Tag: 128 bits (16 bytes) embedded in output
Total Overhead: 28 bytes per encrypted file
```

---

## References
- NIST SP 800-38D (GCM Specification)
- RFC 5116 (AEAD Interface)
- RFC 7539 (ChaCha20 Specification)
- Java Cryptography Architecture (JCA)
- TLS 1.3 (RFC 8446) - uses AES-GCM primarily
