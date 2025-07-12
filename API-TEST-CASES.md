Api test cases
==============

The detailed cURL commands for testing all endpoints of your Secret Stash API, including JWT authentication, are presented below.

A [Postman collection](project.resources/Secret%20Stash.postman_collection.json) is also available to test the API through the GUI.

### 1. User Registration

```bash

curl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{
"email": "user@example.com",
"password": "securePassword123!"
}'
```

### 2. User Login (Get JWT Token)

```bash

curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
"email": "user@example.com",
"password": "securePassword123!"
}'
```
_Save the token from the response for subsequent requests (replace <JWT\_TOKEN> below)_

### 3. Create a Note

```bash

curl -X POST http://localhost:8080/api/notes \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <JWT_TOKEN>" \
-d '{
"title": "My First Secret Note",
"content": "This is my confidential content",
"expiresAt": 1735689600
}'
```

### 4. Get All Notes (Paginated)

```bash

curl -X GET "http://localhost:8080/api/notes?page=0&size=10" \
-H "Authorization: Bearer <JWT_TOKEN>"
```

### 5. Get Latest 1000 Notes

```bash

curl -X GET http://localhost:8080/api/notes/latest \
-H "Authorization: Bearer <JWT_TOKEN>"
```

### 6. Update a Note

```bash

curl -X PUT http://localhost:8080/api/notes/1 \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <JWT_TOKEN>" \
-d '{
"title": "Updated Title",
"content": "Updated content",
"expiresAt": 1735776000
}'
```

### 7. Delete a Note

```bash

curl -X DELETE http://localhost:8080/api/notes/1 \
-H "Authorization: Bearer <JWT_TOKEN>"
```

### Error Case Testing

1.  **Unauthorized Access (No Token)**


```bash

curl -X GET http://localhost:8080/api/notes \
-H "Content-Type: application/json"
```

2.  **Expired Token**


```bash

curl -X GET http://localhost:8080/api/notes \
-H "Authorization: Bearer expired.token.here"
```

3.  **Invalid Note Creation**


```bash

curl -X POST http://localhost:8080/api/notes \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <JWT_TOKEN>" \
-d '{
"title": "",
"content": ""
}'
```

### Tips for Testing:

1.  **Save JWT Token** to environment variable:


```bash

JWT=$(curl -s -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"user@example.com","password":"securePassword123!"}' | jq -r '.token')
```

2.  **Use saved token** in subsequent requests:


```bash

curl -X GET http://localhost:8080/api/notes \
-H "Authorization: Bearer $JWT"
```

3.  **For JSON formatting**, pipe to `jq`:


```bash

curl -s -X GET http://localhost:8080/api/notes | jq
```

4.  **For debugging**, add `-v` flag:


```bash

curl -v -X POST http://localhost:8080/api/auth/login ...
```

5.  **Test rate limiting** (run this command rapidly):


```bash

for i in {1..110}; do
curl -s -X GET http://localhost:8080/api/notes/latest \
-H "Authorization: Bearer $JWT" | jq '. | "Request $i: \(.status / .[0].title)"'
done
```

These commands cover all API endpoints with both success and error cases, including proper JWT authentication handling.