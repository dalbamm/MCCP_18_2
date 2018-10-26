### 1. Database and Django model

​	ORM: Object Relational Mapping

​	-> OOP concept applied in DB, that make data convertible between compatible and incompatible system.

​	Model: python component which is independent to changes in back-end.



####  1) Database basics

##### 	Terminology

  - Record (Row) : Data for a single item.

  - Field (Column) : A part of records

  - Schema : Definition of database including tables, fields, and types.

  - Migration : Change of schema and DB.

  - Primary key : Set of fields which uniquely identify a record in a table.

  - Relationship : Logical connection between different tables.

  - Foreign key : Key for identifying the related record in other tables.

    -> Primary key in the related table.

- One to One : Add a field into the table. (Can be separated as multiple tables)

- Many to One : Add a table which matches the multiple values to one value 

  -> Field of not unique values : Foreign key and Primary key in another table

- Many to Many : Need an additional table that maps the primary keys to foreign keys.

  -> Need 3 tables at least.

#### 2. Use Model in views

- toh/settings.py : add apps in INSTALLED_APPS list / ex. 'hero.apps.HeroConfig',

  -> Project recognize new model and be able to generate migrations and write schema

- Create hero/models.py

  ```
  from django.db import models
  class Hero(models.Model):
  	name = models.CharField(max_length = 120)
  ```

  -> Hero class is registered in db as record. In each *Hero* instance, 'name' member variable is contained and it is a *Field* in *Hero* record.

- Make migrations

  ```
  $ python manage.py makemigrations hero
  ```

  -> Create migration of *model*.​	(Similar concept with *add* in github)

​	-> Keep track of changes in DB schema .

- Migrate

  ```
  $ python manage.py migrate
  ```

  -> Commit all changes of DB schema into DB (Similar concept with *commit* in github)

- Check the model in shell

  ```
  $ python manage.py shell
  ```

- Example codes

  ```
  from hero.models import Hero
  Hero.objects.all()
  hero = Hero(name = 'Superman')
  hero
  hero.name
  hero.save()
  hero.id
  hero.name = 'Batman'
  hero.save()
  hero.name
  ```

- change REPL representation

  ```
  from django.db import models
  class Hero(models.Model):
  	name = models.CharField(max_length=120)
  	def __str__(self):
  		return self.name
  ```

  - QuerySet : Lazily evaluated query results. .exists()/...

- Example codes 2

  ```
  Hero.objects.filter(name='Spiderman')
  Hero.objects.filter(name__endswith='man')
  Hero.objects.get(name='Hulk')
  ```

- Example codes 3 (Written by TAs)

  ```
  from django.http import HttpResponse, JsonResponse, HttpResponseNotAllowed
  from django.http import HttpResponseBadRequest, HttpResponseNotFound
  from django.views.decorators.csrf import csrf_exempt
  from .models import Hero
  import json
  from json.decoder import JSONDecodeError
  
  
  @csrf_exempt
  def hero_list(request):
      if request.method == 'GET':
          hero_all_list = [hero for hero in Hero.objects.all().values()]
          return JsonResponse(hero_all_list, safe=False)
      elif request.method == 'POST':
          try:
              body = request.body.decode()
              hero_name = json.loads(body)['name']
          except (KeyError, JSONDecodeError) as e:
              return HttpResponseBadRequest()
          hero = Hero(name=hero_name)
          hero.save()
          response_dict = {
              'id': hero.id,
              'name': hero.name,
          }
          return JsonResponse(response_dict, status=201)  # created
      else:
          return HttpResponseNotAllowed(['GET', 'POST'])
  
  
  @csrf_exempt
  def hero_detail(request, hero_id):
      if request.method == 'GET':
          try:
              hero = Hero.objects.get(id=hero_id)
          except Hero.DoesNotExist:
              return HttpResponseNotFound()
          response_dict = {
              'id': hero.id,
              'name': hero.name,
          }
          return JsonResponse(response_dict)
      elif request.method == 'PUT':
          try:
              hero = Hero.objects.get(id=hero_id)
          except Hero.DoesNotExist:
              return HttpResponseNotFound()
  
          try:
              body = request.body.decode()
              hero_name = json.loads(body)['name']
          except (KeyError, JSONDecodeError) as e:
              return HttpResponseBadRequest()
  
          hero.name = hero_name
          hero.save()
          return HttpResponse(status=204)
      elif request.method == 'DELETE':
          try:
              hero = Hero.objects.get(id=hero_id)
          except Hero.DoesNotExist:
              return HttpResponseNotFound()
  
          hero.delete()
          return HttpResponse(status=204)
      else:
          return HttpResponseNotAllowed(['GET', 'PUT', 'DELETE'])
  
  ```

  Hero.objects.all().values() --> returns list of python dictionary, not the model object. (Compatible form with JSON, serialization)

- JSON(JavaScript Object Notation)

  Readable, Text format, Can represent common data structure

  return JsonResponse(hero_all_list, safe = False) --> Similar with HttpResponse, and returns JSON-serialized data. / safe = false is needed when send list

- Summary :

  -> Deserialize the request body with json.loads / .decode() 

#### 3. Relationship

- on_delete = models.CASCADE --> meaning that this instance can not exist without team leader.

​			when the team leader is deleted from team, then Team instance would be deleted.

- related_name... Team 내의 function으로 만들어도 마치 Hero 내의 function으로 기능. 