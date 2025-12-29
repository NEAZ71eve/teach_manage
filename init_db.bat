@echo off
cd "C:\Program Files\MySQL\MySQL Server 9.5\bin"
mysql -u root -p54088Cnm, major_management < "C:\Users\21516\Documents\trae_projects\DBS\spring-boot-vue-course-management\backend\src\main\resources\full-init-database.sql"
echo Database initialization completed!
pause