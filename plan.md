# FileShare System OOAD Plan (Java)

## 1) Requirement (Build a FileShare System (save files, restore files, delete files, listFiles, Search) with encryption)
- Save file (encrypt + store metadata)
- Restore file (decrypt + return)
- Delete file (logical + physical)
- List files (per user / scope)
- Search (by name, tags, owner, date, size)

## 2) Domain model (key entities)
- `User` (id, name, roles)
- `File` (id, ownerId, name, size, createdAt, checksum, status)
- `FileVersion` (id, fileId, version, encryptedPath, keyId, iv, checksum)
- `AccessPermission` (fileId, grants)
- `SearchIndexEntry` (fileId, tokens, tags)

## 3) Service layer (core OOAD classes)
- `FileService` (save, restore, delete, list, search)
- `EncryptionService` (encrypt, decrypt, generateKey, rotateKey)
- `StorageService` (store, retrieve, delete)
- `MetadataRepository` (CRUD for `File`, `FileVersion`)
- `SearchService` (index, query)
- `AuditService` (log actions)

## 4) Key relationships
- `FileService` orchestrates `EncryptionService`, `StorageService`, `MetadataRepository`, `SearchService`.
- `File` has many `FileVersion`.
- `AccessPermission` linked to `File`.

## 5) Use-case flows (sequence outline)
- **Save**
  1) validate input + permission  
  2) `EncryptionService.encrypt(stream)`  
  3) `StorageService.store(ciphertext)`  
  4) `MetadataRepository.create(File, FileVersion)`  
  5) `SearchService.index()`
- **Restore**
  1) authorize  
  2) `StorageService.retrieve()`  
  3) `EncryptionService.decrypt()`
- **Delete**
  1) authorize  
  2) mark `File.status=DELETED`  
  3) `StorageService.delete()`  
  4) de-index
- **List/Search**
  1) query metadata and/or search index  
  2) filter by policy

## 6) Design principles (OOAD)
- Use interfaces for services (`IEncryptionService`, `IStorageService`)
- Use repositories for persistence boundaries
- Keep `FileService` as an application service; no I/O details inside
- Define DTOs for requests/responses

## 7) Non-functional
- Encryption: AES-GCM, per-file key, key management strategy
- Audit trail: log all access events
- Scalability: store files in filesystem or object storage abstraction
