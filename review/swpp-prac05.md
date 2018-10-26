### 1. Travis Continuous Integration

- .travis.yml file 수정함. - http://yaml.org/ 참고 

  ex.

  ```
  language: python
  script:
  	- echo "build start!"
  	- python ./main.py
  ```

### 2. Introduction to Django

- MVC model: controller ->(Manipulate) model ->(Update) view ->(Seen by ) User ->(Uses) Contoroller



- Architecture: 

  Client sends the HTTP request ->

  Angular sends the request to Django when backend service is needed.(JSON) ->

  Django handles the request (Access DB and build the response data). ->

  Data is passed to the frontend.(from django to angular, formatted as JSON) ->

  Angular renders the view(HTML). ->

  (A user see the change in the view which is a response of the own previous request)

- Start a Django project

  ```
  $ django-admin startproject toh
  $ cd toh
  ```

  Generate a project directory named as "toh"

  - manage.py : A script for managing toh project.
  - toh directory: Overall settings of toh project.



- Check my project

  ```
  ($ python manage.py makemigration)
  $ python manage.py migrate
  $ python manage.py runserver
  $ curl http://localhost:8000
  ```



- Start a Django app

  ```
  $ python manage.py startapp hero
  ```

  Make an app named 'hero' under my project

  - Project vs App:

    Project - A collection of configuration and apps for a particular website. (Multiple apps in one.)

    App - Web application that take an action. Pluggable, thus can be referenced from multiple pjts.



- Edit hero/view.py

  ```
  from django.http import HttpResponse
  def index(request):
  	return HttpResponse('Hello, world!)
  ```

  -> The view in hero app is changed (Print a written text).



- Define routes (Edit hero/urls.py)

  - Create hero/urls.py

  ```
  from django.urls import path
  from . import views
  
  urlpatterns = [path(''), views.index, name='index']
  ```

  ->  views.index: Call *index* function from view.py

  - Edit toh/urls.py

  ```
  from django.contrib import admin
  from django.urls import include, path
  
  urlpatterns = [ 
  	path('hero/', include('hero.urls')),
  	path('admin/', admin.site.urls),		
  ]
  ```

  -> include : Literally include another url configuration, not only a url text, but also a path list.

  -> result: "Hello, world!" is printed on http://localhost:8000/hero/ 



​	-> HTTP request GET /hero/ to django

​	-> Django scans matching url configurations from a root url configuration file, toh/urls.py

​	-> Django continues to scan in hero/urls.py in according to toh/urls.py

​	-> Django invokes the function index in hero/views.py with *HTTP request* context. (index(HTTP request))

​	-> Send the return value of view function back as a response from Django to Client.



- Advanced URL configuration 

```
urlpatterns = [
    path('articles/2003/', views.~~),
    path('articles/<int:year>/<int:month>/<slug:slug>', views.~~),   
]
```

articles/(Grab the int variable in name of 'year')/(Grab the int variable in name of 'month')/(Grab the slug variable in name of 'slug')/ -> goes to views.py and execute ~~ function.