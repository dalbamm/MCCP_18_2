on_delete = models.CASCADE --> meaning that this instance can not exist without team leader.

​			when the team leader is deleted from team, then Team instance would be deleted.

related_name... Team 내의 function으로 만들어도 마치 Hero 내의 function으로 기능. 