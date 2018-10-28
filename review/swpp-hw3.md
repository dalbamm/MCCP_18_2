# SWPP-hw3-dalbamm (2018: 10.28~11.2)

### 1. Angular testing

### 2. Django implementation

- ##### Create a model named article

  Add below codes in /blog/models.py

  ```
  class Article(models.Model):
  	title = models.CharField(max_length=120)
  	content = models.TextField(max_length=500)
  	author = models.ForeignKey(
  			User,
  			on_delete=models.CASCADE,
  			related_name='author_set',
  	)
  	def __str__(self):
  		return "title: {}/ content: {}".format(self.title, self.content)
  
  class Comment(models.Model):
  	article = models.ForeignKey(
  		Article,
  		on_delete=models.CASCADE,
  		related_name='article_set'
  	)
  	content = models.TextField(max_length=500)
  	author = models.ForeignKey(
  			User,
  			on_delete=models.CASCADE,
  			related_name='comment_author_set',
  	)
  	def __str__(self):
  		return "content: {}".format(self.content)
  ```

  *User* class is imported from default User model. 

  Remember that ForeignKey needs 2 or 3 arguments to be initialized, which are type and on_delete configuration. *related_name* is a flexible argument, that helps find the variable set.