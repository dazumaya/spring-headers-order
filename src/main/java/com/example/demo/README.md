# spring-headers-order

## API

### `/headers`
 
spring がヘッダーを受け取った順で array にして返却する.  
curl のリクエストヘッダーとレスポンスを見比べることで, クライアントとサーバーの間でヘッダーの順序性が保たれていることを確認する.  

### `/proxy`

spring がヘッダーを受け取った順でオリジンにプロキシーする.  
そのとき, オリジンに対してもヘッダーの順序性が保たれていることを確認する.  
`/headers` がヘッダーの順序性が保っている前提で, オリジンを `/headers` にすることにより,  
curl のリクエストヘッダーとレスポンスを見比べることで, サーバーとオリジンの間でヘッダーの順序性が保たれていることを確認する.  

## Result

### `/headers` ヘッダー重複なし

```console
❯ curl http://localhost:8080/headers -H 'Hoge: 1' -H 'Fuga: 2' -H 'Foo: 3' -H 'Bar: 4' -v
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /headers HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.64.1
> Accept: */*
> Hoge: 1
> Fuga: 2
> Foo: 3
> Bar: 4
>
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Sat, 01 May 2021 03:08:51 GMT
<
* Connection #0 to host localhost left intact
[ [ "host", "localhost:8080" ], [ "user-agent", "curl/7.64.1" ], [ "accept", "*/*" ], [ "hoge", "1" ], [ "fuga", "2" ], [ "foo", "3" ], [ "bar", "4" ] ]* Closing connection 0
```

### `/proxy` ヘッダー重複なし

```console
❯ curl http://localhost:8080/proxy -H 'Hoge: 1' -H 'Fuga: 2' -H 'Foo: 3' -H 'Bar: 4' -v
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /proxy HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.64.1
> Accept: */*
> Hoge: 1
> Fuga: 2
> Foo: 3
> Bar: 4
>
< HTTP/1.1 200
< Content-Type: application/json
< Content-Length: 152
< Date: Sat, 01 May 2021 03:08:04 GMT
<
* Connection #0 to host localhost left intact
[ [ "host", "localhost:8080" ], [ "user-agent", "curl/7.64.1" ], [ "accept", "*/*" ], [ "hoge", "1" ], [ "fuga", "2" ], [ "foo", "3" ], [ "bar", "4" ] ]* Closing connection 0
```

### `/headers` ヘッダー重複あり

```
❯ curl http://localhost:8080/headers -H 'Hoge: 1' -H 'Fuga: 2' -H 'Foo: 3' -H 'Bar: 4' -H 'Foo: 5' -v
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /headers HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.64.1
> Accept: */*
> Hoge: 1
> Fuga: 2
> Foo: 3
> Bar: 4
> Foo: 5
>
< HTTP/1.1 200
< Content-Type: application/json
< Transfer-Encoding: chunked
< Date: Sat, 01 May 2021 03:09:40 GMT
<
* Connection #0 to host localhost left intact
[ [ "host", "localhost:8080" ], [ "user-agent", "curl/7.64.1" ], [ "accept", "*/*" ], [ "hoge", "1" ], [ "fuga", "2" ], [ "foo", "3" ], [ "foo", "5" ], [ "bar", "4" ] ]* Closing connection 0
```

### `/proxy` ヘッダー重複あり

```console
❯ curl http://localhost:8080/proxy -H 'Hoge: 1' -H 'Fuga: 2' -H 'Foo: 3' -H 'Bar: 4' -H 'Foo: 5' -v
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /proxy HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.64.1
> Accept: */*
> Hoge: 1
> Fuga: 2
> Foo: 3
> Bar: 4
> Foo: 5
>
< HTTP/1.1 200
< Content-Type: application/json
< Content-Length: 168
< Date: Sat, 01 May 2021 03:10:16 GMT
<
* Connection #0 to host localhost left intact
[ [ "host", "localhost:8080" ], [ "user-agent", "curl/7.64.1" ], [ "accept", "*/*" ], [ "hoge", "1" ], [ "fuga", "2" ], [ "foo", "3" ], [ "foo", "5" ], [ "bar", "4" ] ]* Closing connection 0
```

## Conclusion

* ヘッダー名が小文字になってしまう
  * すべて HTTP/1.1 とみなし、ヘッダー名を正規化することはできる
    * `accept-encoding` => `Accept-Encoding`
      * https://golang.org/pkg/net/http/#CanonicalHeaderKey
* ヘッダー重複なしのとき、ヘッダー順を維持することができる
* ヘッダー重複ありのとき、ヘッダー順を維持することができない
  * 同名のヘッダーは、その値がマージされる
  * リクエストヘッダーの重複は通常はないはず
