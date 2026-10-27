#!/bin/sh
set -eu

# Calls the admin API and prints the JSON response on a single line, without whitespace.
# Returns a non-zero status when the request fails.
api() {
  method=$1; path=$2; shift 2
  response=$(curl -sS -f -X "$method" -H "Authorization: Bearer $GARAGE_ADMIN_TOKEN" \
    -H "Content-Type: application/json" "$@" "$GARAGE_ADMIN_URL$path") || return 1
  printf '%s' "$response" | tr -d ' \n'
}

echo "Waiting for Garage..."
until api GET /v2/GetClusterHealth > /dev/null 2>&1; do sleep 1; done

layout=$(api GET /v2/GetClusterLayout)
if echo "$layout" | grep -q '"roles":\[\]'; then
  node_id=$(api GET /v2/GetClusterStatus | sed -n 's/.*"nodes":\[{"id":"\([0-9a-f]*\)".*/\1/p')
  version=$(echo "$layout" | sed -n 's/.*"version":\([0-9]*\).*/\1/p')
  echo "Assigning layout to node $node_id"
  api POST /v2/UpdateClusterLayout -d "{\"roles\":[{\"id\":\"$node_id\",\"zone\":\"dc1\",\"capacity\":1073741824,\"tags\":[]}]}" > /dev/null
  api POST /v2/ApplyClusterLayout -d "{\"version\":$((version + 1))}" > /dev/null
fi

if ! api GET "/v2/GetKeyInfo?id=$S3_ACCESS_KEY" > /dev/null 2>&1; then
  echo "Importing access key $S3_ACCESS_KEY"
  api POST /v2/ImportKey -d "{\"accessKeyId\":\"$S3_ACCESS_KEY\",\"secretAccessKey\":\"$S3_SECRET_KEY\",\"name\":\"avis-stades-local\"}" > /dev/null
fi

if ! bucket=$(api GET "/v2/GetBucketInfo?globalAlias=$S3_BUCKET" 2>/dev/null); then
  echo "Creating bucket $S3_BUCKET"
  bucket=$(api POST /v2/CreateBucket -d "{\"globalAlias\":\"$S3_BUCKET\"}")
fi
bucket_id=$(echo "$bucket" | sed -n 's/^{"id":"\([0-9a-f]*\)".*/\1/p')

api POST /v2/AllowBucketKey -d "{\"bucketId\":\"$bucket_id\",\"accessKeyId\":\"$S3_ACCESS_KEY\",\"permissions\":{\"read\":true,\"write\":true,\"owner\":true}}" > /dev/null
echo "Garage ready: bucket $S3_BUCKET, key $S3_ACCESS_KEY"
