### swpp - prac02~03 review

###### slide 35 : 	

1. app.module.ts --> module의 종류와 경로 관리.
   - ​	새로운 app module 추가시 --module flag를 부여하면 app.module.ts에 자동으로 insertion 발생.	
2. app-routing.module.ts --> RouterModule.forRoot(routes) : routes의 root로 RouterModule로..

###### slide 36	:

1. ​	app.component.html 변경 --> localhost:4200/heroes  링크 활성화
   - /heroes link를 root의 하위 directory로 linking하는 역할. 

###### slide 37 :

1. ng generate component dashboard 입력 -> app/dashboard/~.component.~ 생김 + app.module.ts 업데이트
2. dashboard.component.ts update.  -> ./heroes에 list가 1개 더생김..
3. ~.component.html update -> ??
4. ~.css update -> ??

###### slide 38 :

1. app-routing module~.ts update : path ''에 redirectTo '~'를 설정 ->  root가 root/~으로 자동 이동
   - 위에 37 slide .css, .html file은 dashboard instance가 path를 통해 이용되기 시작한 지금의 update에서야 이용되기 시작함.
2. app.component.html update : routerLink 추가 -> dashboard view에 Heroes 옆에 Dashboard 추가됨 

###### slide 39 :

​	app-routing.module.ts에 detail directory 에 HeroDetailComponent 연결성 추가

###### slide 40 : 

1. dashboard.~.html 에 router link 추가..-> 배너 클릭시 해당 hero의 detail/id 링크로 이동
2. heroes.~.html에 routerLink 추가 -> 각 element에 hyperlink 추가됨. 누르면 detail/id로 이동

###### 
