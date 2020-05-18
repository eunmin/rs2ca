(ns rs2ca.core
  (:require [clojure.core.async :refer [chan <!! put! close! >!! go <! >! buffer]])
  (:import [java.util.function BiFunction]
           [io.r2dbc.spi ConnectionFactories ConnectionFactoryOptions]
           [org.reactivestreams Publisher Subscriber Subscription]))

(defn pub->chan [p]
  (let [c (chan (buffer 1))
        subscription (atom nil)]
    (.subscribe p (proxy [Subscriber] []
                    (onSubscribe [s]
                      (reset! subscription s)
                      (.request @subscription 1))
                    (onNext [o]
;;                      (println "next:" o "thread:" (.getName (Thread/currentThread)))
                      (put! c o)
                      (.request @subscription 1))
                    (onError [t]
                      (throw t))
                    (onComplete []
                      (close! c))))
    c))


