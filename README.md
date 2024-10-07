# 📲 매장 테이블 예약 서비스 프로젝트

+ 매장을 방문할때 미리 방문 예약을 진행하는 기능입니다.

# 📆 프로젝트 기간
+ 24.09.16 ~ 24.10.07

# 🛠️ 기술 스택

+ **Language** : JAVA
+ **Framework** : Spring Boot
+ **Database** : MySql
+ **Build** : GRADLE
+ **Persistence** : Spring Data JPA
+ **Test** : JUnit
+ **JDK** : OpenJDK-17

# 🔴 주요 기능 설명

![image](https://github.com/user-attachments/assets/ef1f97ed-db48-40b8-9af6-9cc2c38eca29)

+ 파트너 가입: 점주은 파트너 회원 가입 후에 서비스를 이용할 수 있습니다.
+ 매장 등록 기능: 점장은 매장 명, 상점 위치, 상점 설명 등의 정보를 입력하여 매장을 등록합니다.
+ 매장 검색 및 상세 정보 확인 기능: 매장 이용자는 앱을 통해 매장을 검색하고, 상세 정보를 확인할 수 있습니다.
+ 예약 기능: 이용자는 매장의 상세 정보를 보고, 예약을 진행합니다. 예약을 진행하기 위해서는 회원 가입이 필수입니다.
+ 방문 확인 기능: 예약 후 10분 전에 도착하여 키오스크를 통해 방문 확인을 진행합니다.
+ 리뷰 작성 기능: 이용자는 예약 및 매장 이용 후 리뷰를 작성할 수 있습니다. 리뷰 수정은 작성자만 가능하며, 삭제는 작성자와 매장의 관리자(점장 등)만 할 수 있습니다.

# 🧾 ERD
![image](https://github.com/user-attachments/assets/922acdaa-36d9-4a97-a6dc-df5e9c79431f)

# 💻 최종 구현 API

## 파트너

 + ✅ POST - /partner/register (회원 가입)
 + ✅ POST - /partner/login (로그인)
 + ✅ POST - /partner/logout (로그아웃)

## 유저

 + ✅ POST - /user/register (회원 가입)
 + ✅ POST - /user/login (로그인)
 + ✅ POST - /user/logout (로그아웃)

## 매장

 + ✅ POST - /store/register (상점 등록)
 + ✅ PUT - /store/update/{storeId} (상점 수정)
 + ✅ DELETE - /store/delete/{storeId} (상점 삭제)
 + ✅ GET - /store/search (상점 검색)
 + ✅ GET - /store/detail/{storeId} (상점 상세 정보)

## 예약

 + ✅ POST - /reservation (예약 등록)
 + ✅ POST - /reservation/confirm (예약 확인)
 + ✅ PUT- /reservation/approve/{reservationId} (예약 승인)
 + ✅ PUT - /reservation/reject/{reservationId} (예약 거절)

## 리뷰

 + ✅ POST - /review (리뷰 작성)
 + ✅ GET - /review (리뷰 15개씩 확인)
 + ✅ GET - /review/{reviewId} (상세 리뷰)
 + ✅ PUT - /review/{reviewId} (리뷰 수정)
 + ✅ DELETE - /review/{reviewId} (리뷰 삭제)


