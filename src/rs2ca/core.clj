(ns rs2ca.core
  (:require [clojure.core.async :refer [chan <!! put! close! >!! go <! >! buffer]])
  (:import [java.util.function BiFunction]
           [io.r2dbc.spi ConnectionFactories ConnectionFactoryOptions]
           [org.reactivestreams Publisher Subscriber Subscription]
           [com.mongodb.reactivestreams.client
            MongoClient
            MongoClients
            MongoCollection
            MongoDatabase]))

(defn pub->chan [p]
  (let [c (chan (buffer 1))
        subscription (atom nil)]
    (.subscribe p (proxy [Subscriber] []
                    (onSubscribe [s]
                      (reset! subscription s)
                      (.request @subscription 1))
                    (onNext [o]
                      (put! c o)
                      (.request @subscription 1))
                    (onError [t]
                      (throw t))
                    (onComplete []
                      (close! c))))
    c))

;; Async postgresql client example

(def options
  (let [bd (ConnectionFactoryOptions/builder)]
    (.option bd ConnectionFactoryOptions/DRIVER "postgresql")
    (.option bd ConnectionFactoryOptions/HOST "localhost")
    (.option bd ConnectionFactoryOptions/PORT (int 5432))
    (.option bd ConnectionFactoryOptions/DATABASE "test")
    (.build bd)))

(def factory (ConnectionFactories/get options))

(defn run []
  (go
    (let [conn (<! (pub->chan (.create factory)))
          result (<! (pub->chan (.execute (.createStatement conn "select * from users"))))
          rows (pub->chan (.map result (proxy [BiFunction] []
                                         (apply [row meta]
                                           (.get row "name")))))]
      (println (<! rows))
      (println (<! rows)))))

;; Async Mongoclient example

(def client (MongoClients/create))
(def db (.getDatabase client "test"))
(def coll (.getCollection db "users"))

(defn run2 []
  (go
    (let [doc (<! (pub->chan (.find coll)))]
      (println doc))))
