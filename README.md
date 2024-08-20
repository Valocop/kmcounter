# kmcounter

# 1. Start postgres container docker-compose.yml `docker-compose up -d`

# 2. Start application km-counter `gradle run`

# 3. Examples of queries:

# Create

`
curl -X POST "http://127.0.0.1:88/Create" \
--header 'Content-Type: application/json' \
--data-raw '{"name":"counter1", "counter":1}'
`

# Read

`curl -X GET "http://127.0.0.1:88/Get?counter=counter1"`

# Delete

`curl -X POST "http://127.0.0.1:88/Delete?counter=counter1"`

# Increment

`curl -X POST "http://127.0.0.1:88/Increment?counter=counter1"`

# GetAll

`curl -X GET "http://127.0.0.1:88/GetAll"`