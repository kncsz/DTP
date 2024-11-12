#package impl is deprecated
#在后端开发中，返回的状态码有助于客户端理解请求的结果。除了标准的 HTTP 状态码外，程序员可以定义自定义的状态码以适应特定的业务需求。以下是一些常见的标准 HTTP 状态码和程序员可能自定义的状态码示例：

### 标准 HTTP 状态码

1. **2xx 成功系列**
    - `200 OK`：请求成功，服务器返回请求的数据。
    - `201 Created`：请求成功，服务器创建了资源。
    - `204 No Content`：请求成功，但服务器没有返回内容。

2. **4xx 客户端错误系列**
    - `400 Bad Request`：请求无效或格式错误。
    - `401 Unauthorized`：请求未授权，需要身份验证。
    - `403 Forbidden`：服务器拒绝请求，客户端没有权限。
    - `404 Not Found`：请求的资源未找到。
    - `409 Conflict`：请求与当前服务器状态冲突（如资源冲突）。

3. **5xx 服务器错误系列**
    - `500 Internal Server Error`：服务器内部错误，无法完成请求。
    - `502 Bad Gateway`：作为网关的服务器从上游服务器接收到无效响应。
    - `503 Service Unavailable`：服务不可用，可能是由于服务器过载或维护。
    - `504 Gateway Timeout`：作为网关的服务器未能及时从上游服务器接收到响应。

### 自定义状态码示例

自定义状态码通常用来处理特定的业务逻辑或应用程序状态，标准 HTTP 状态码无法覆盖所有应用场景。以下是一些示例：

1. **应用特定错误**
    - `422 Unprocessable Entity`：请求格式正确，但由于语义错误，无法处理（虽然这不是完全自定义，但在一些应用中被用来表示处理问题）。

2. **业务逻辑状态**
    - `450 User Blocked`：用户因某些原因被阻止（这不是标准状态码，但可以自定义）。
    - `451 Unavailable For Legal Reasons`：由于法律问题，资源不可用（这是标准状态码，使用时要符合规范）。

3. **认证和授权**
    - `470 User Account Locked`：用户账户被锁定。
    - `471 Insufficient Permissions`：用户权限不足以执行请求的操作。

4. **请求处理**
    - `480 Request Timeout`：请求超时（如果需要比标准的 `408 Request Timeout` 更细粒度的控制）。
    - `490 Invalid Token`：提供的令牌无效或过期。

5. **特定业务需求**
    - `499 Client Closed Request`：客户端关闭请求连接（用于跟踪客户端行为）。
    - `599 Network Connect Timeout Error`：网络连接超时错误（用于特定情况的详细错误报告）。

