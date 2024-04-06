(ns app.plumbing.s3
  (:import
   (java.time Duration)
   (software.amazon.awssdk.auth.credentials AwsBasicCredentials
                                            StaticCredentialsProvider)
   (software.amazon.awssdk.services.s3 S3Client)
   (software.amazon.awssdk.regions Region)
   (software.amazon.awssdk.services.s3.presigner S3Presigner)
   (software.amazon.awssdk.services.s3.model GetObjectRequest
                                             PutObjectRequest
                                             PutObjectResponse)
   (software.amazon.awssdk.services.s3.presigner.model GetObjectPresignRequest
                                                       PresignedGetObjectRequest
                                                       PutObjectPresignRequest
                                                       PresignedPutObjectRequest)))

#_(def cred "how the creds for s3 is formated"
    {:credentials {:access-key "****"
                   :secret-key "****"
                   :region     "ap-southeast-1"}})

(defn create-s3-presigner
  [{:keys [credentials]}]
  (let [^Region region                (case (:region credentials)
                                        "ap-southeast-3" Region/AP_SOUTHEAST_3
                                        "ap-southeast-1" Region/AP_SOUTHEAST_1)
        ^AwsBasicCredentials awsCreds (AwsBasicCredentials/create (:access-key credentials)
                                                                  (:secret-key credentials))
        ^S3Presigner presigner        (.build (doto (S3Presigner/builder)
                                                (.region region)
                                                (.credentialsProvider (StaticCredentialsProvider/create awsCreds))))]
    presigner))

(defn generate-upload-url
  [presigner bucket key content-type duration-minutes]
  (let [^PutObjectRequest por           (.build (doto (PutObjectRequest/builder)
                                                  (.bucket bucket)
                                                  (.key key)
                                                  (.contentType content-type)))
        ^PutObjectPresignRequest popr   (.build (doto (PutObjectPresignRequest/builder)
                                                  (.signatureDuration (Duration/ofMinutes duration-minutes))
                                                  (.putObjectRequest por)))
        ^PresignedPutObjectRequest ppor (.presignPutObject presigner popr)]
    (.toString (.url ppor))))

(comment
  "sample function args, this bucket only for arifian's testing"

  (generate-upload-url (create-s3-presigner cred) "zenpro-x-backsite-assets-staging" "path/to/file.txt" "text/plain" 5)

  ;; curl -X PUT -H "Content-Type: text/plain" --upload-file test.txt "https://arifian-test-bucket.s3.ap-southeast-1.amazonaws.com/path/to/file.txt?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20230718T082115Z&X-Amz-SignedHeaders=content-type%3Bhost&X-Amz-Expires=518400&X-Amz-Credential=AKIATCASCLYJY37X6LNL%2F20230718%2Fap-southeast-1%2Fs3%2Faws4_request&X-Amz-Signature=a8d1d9ee0e030d6fa092529f34937a608df4dabb0c1744c2256f1c67bc868c8d"
  )

