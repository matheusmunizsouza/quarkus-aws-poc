provider "aws" {
  region                      = "us-east-1"
  access_key                  = "localstack"
  secret_key                  = "localstack"
  skip_credentials_validation = true
  skip_requesting_account_id  = true
  skip_metadata_api_check     = true
  s3_force_path_style         = true
  endpoints {
    sqs      = "http://localhost:4566"
    dynamodb = "http://localhost:4566"
  }
}

resource "aws_sqs_queue" "aws_example_queue" {
  name                      = "aws-example-queue"
  delay_seconds             = 15
  max_message_size          = 2048
  message_retention_seconds = 86400
  receive_wait_time_seconds = 10
}

resource "aws_dynamodb_table" "dynamodb-table" {
  name           = "Book"
  billing_mode   = "PROVISIONED"
  read_capacity  = 20
  write_capacity = 20
  hash_key       = "Isbn"
  range_key      = "Name"

  attribute {
    name = "Isbn"
    type = "S"
  }

  attribute {
    name = "Name"
    type = "S"
  }
}