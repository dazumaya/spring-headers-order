# spring-headers-order

Spring Boot + Jetty でヘッダー順序維持を検証する.  
Server を Jetty に変更して、baseRequest からリクエストヘッダーを取得する.  

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
< HTTP/1.1 200 OK
< Date: Sun, 30 May 2021 10:53:00 GMT
< Content-Type: application/json
< Transfer-Encoding: chunked
<
* Connection #0 to host localhost left intact
[ [ "Host", "localhost:8080" ], [ "User-Agent", "curl/7.64.1" ], [ "Accept", "*/*" ], [ "Hoge", "1" ], [ "Fuga", "2" ], [ "Foo", "3" ], [ "Bar", "4" ] ]* Closing connection 0
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
< HTTP/1.1 200 OK
< Date: Sun, 30 May 2021 10:53:22 GMT
< Content-Type: application/json
< Content-Length: 152
<
* Connection #0 to host localhost left intact
[ [ "Host", "localhost:8080" ], [ "User-Agent", "curl/7.64.1" ], [ "Accept", "*/*" ], [ "Hoge", "1" ], [ "Fuga", "2" ], [ "Foo", "3" ], [ "Bar", "4" ] ]* Closing connection 0
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
< HTTP/1.1 200 OK
< Date: Sun, 30 May 2021 10:53:46 GMT
< Content-Type: application/json
< Transfer-Encoding: chunked
<
* Connection #0 to host localhost left intact
[ [ "Host", "localhost:8080" ], [ "User-Agent", "curl/7.64.1" ], [ "Accept", "*/*" ], [ "Hoge", "1" ], [ "Fuga", "2" ], [ "Foo", "3" ], [ "Bar", "4" ], [ "Foo", "5" ] ]* Closing connection 0
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
< HTTP/1.1 200 OK
< Date: Sun, 30 May 2021 10:54:07 GMT
< Content-Type: application/json
< Content-Length: 168
<
* Connection #0 to host localhost left intact
[ [ "Host", "localhost:8080" ], [ "User-Agent", "curl/7.64.1" ], [ "Accept", "*/*" ], [ "Hoge", "1" ], [ "Fuga", "2" ], [ "Foo", "3" ], [ "Bar", "4" ], [ "Foo", "5" ] ]* Closing connection 0
```

### `/proxy` ヘッダー重複あり (大文字・小文字混在)

```
❯ curl http://localhost:8080/proxy -H 'Hoge: 1' -H 'Fuga: 2' -H 'Foo: 3' -H 'Bar: 4' -H 'Foo: 5' -H 'bar: 6' -H 'hOGE: 7' -v
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
> bar: 6
> hOGE: 7
>
< HTTP/1.1 200 OK
< Date: Sun, 30 May 2021 10:52:42 GMT
< Content-Type: application/json
< Content-Length: 201
<
* Connection #0 to host localhost left intact
[ [ "Host", "localhost:8080" ], [ "User-Agent", "curl/7.64.1" ], [ "Accept", "*/*" ], [ "Hoge", "1" ], [ "Fuga", "2" ], [ "Foo", "3" ], [ "Bar", "4" ], [ "Foo", "5" ], [ "bar", "6" ], [ "hOGE", "7" ] ]* Closing connection 0
```

## Conclusion

* ヘッダー順序が維持される
  * 同名ヘッダーの値もマージされず状態が保たれる
  * ヘッダー名の大文字・小文字も状態が保たれる
