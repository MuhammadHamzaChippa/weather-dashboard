(ns dashboard.helper
  (:require [reagent.core :as r]))

;helper functions

(defn convert-day
  "Convert day number to day name" 
  [day-int]
  (cond
    (= day-int 0) "Sunday"
    (= day-int 1) "Monday"
    (= day-int 2) "Tuesday"
    (= day-int 3) "Wednesday"
    (= day-int 4) "Thursday"
    (= day-int 5) "Friday"
    (= day-int 6) "Saturday"))

(defn convert-month 
  "Convert month number to month name"
  [month-int]
  (cond
    (= month-int 0) "January"
    (= month-int 1) "February"
    (= month-int 2) "March"
    (= month-int 3) "April"
    (= month-int 4) "May"
    (= month-int 5) "June"
    (= month-int 6) "July"
    (= month-int 7) "August"
    (= month-int 8) "September"
    (= month-int 9) "October"
    (= month-int 10) "November"
    (= month-int 11) "Devember"))

(defn encode-time 
  "convert single digit to doublt digit by adding zero"
  [int]
  (if (= (count (str int)) 1)
    (str "0" int)
    int))

(defn celsius-to-fahrenheit 
  "convert celsius to fahrenheite"
  [celsius]
  (+  (* celsius  1.8) 32))

(defn condition-to-image-url 
  "generate image url from weather condition"
  [condition]
  (cond
    (= condition "Thunderstorm") "./images/Thunderstorm.png"
    (= condition "Drizzle") "./images/Drizzle.png"
    (= condition "Rain") "./images/Rain.png"
    (= condition "Snow") "./images/Snow.png"
    (= condition "Clear") "./images/Clear.png"
    (= condition "Clouds") "./images/cloud.png"
    :else "./images/Mist.png"))

(defn extract-detail 
  "given the detail map and the key, get the value on the basis of search-key"
  [details-map key search-key]
  (first (filter #(not= % nil) (map (fn [x] (when (= (:max x) search-key) ((keyword key) x))) details-map)))
  )

(defn date-to-time 
  "Convert js/Date object into formatted time string"
  [date]
  (str (encode-time (.getHours date)) ":" (encode-time (.getMinutes date)) ":" (encode-time (.getSeconds date)))
)
  
  
