# SWPP-hw2-dalbamm (10.08~10.12)

### 1. sign_in page 만들기

- root를 /sign_in 으로 routing & redirection 시킨다.

  - app-messages를 추가하면 화면이 안뜨는 이상현상.

  - /sign_in link도 창이 뜨기는 하지만, 루트가 리디렉션되지는 않았음..

    #### -> signin component를 만들어주고 directory 연결했더니 해결됨.

- /sign_in에 input 2개, button 1개를 넣는다.

  - <input type="text" name="email-input"></input>했는데 안나옴

    #### -> </input>이 잘못된 양식이었음

  - <button id="">Sign in</button>

    #### ->commit 1

- button의 click event를 class에 연동이 잘 안됐다

- getElementById는 html상의 input member가 initializing 된 이후에 이용가능하다.

  - **Your code is parsed line by line, and the lol = ... line is evaluated before the browser knows about the existance of an input with id lolz. Thus, document.getElementById('lolz')will return null, and document.getElementById('lolz').value should cause an error.**

  #### -> (click)="hello()" onclick X onClick X...

  #### ----------------해결: var email = document.getElementById('email-input').value; + @html : (click)="check()"..var를 callee function 안에서 지정하는 것이 중요한듯. - 코드 초기화 이후의 실행을 보장

  ###### minor error: TS2339 can be solved by below casting

  ###### "var inputValue = (<HTMLInputElement>document.getElementById(elementId)).value;"

- #### 중요 포인트 3개 : 

  - #### javascript는 function을 argument로 받을 수 있다.

    - 인풋과 아웃풋은 function or method에 달려있음

      --> observable.subscribe().. observable이 wrapping하고 있는 object를 전달해줌.

    - comments = array.filter(comment => comment.article_id === this.article.id )

      --> filter 내의 argument function의 output이 boolean이며 결과를 true로 만드는 element만 선별해서 comments에 assign.

    - this.comments = comments

      --> ~~.subscribe( comments => this.comments = comments.filter( comment => comment.article_id === this.article.id ) )

  - #### 그 function은 lambda function도 가능하다.

  - #### lambda function 양식상 variable 선언이 불필요하다.

    - using fat arrow.  (comment) => this.comment = comment

      --> comment를 인자로 받아서, this.comment에 comment를 assign하는 lamda func.
