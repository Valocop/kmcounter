# kmcounter

curl "http://127.0.0.1:88/Create" --header 'Content-Type: application/json' --request POST --data-binary '{"name": "counter1","counter": 1}' --insecure

curl -X GET "http://127.0.0.1:88/Get?counter=counter1" --insecure

curl "http://127.0.0.1:88/Delete?counter=counter1" --request POST --insecure