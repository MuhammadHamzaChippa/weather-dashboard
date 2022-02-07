(ns dashboard.card
  (:require [reagent.core :as r]
            [dashboard.helper :refer [condition-to-image-url convert-day convert-month date-to-time celsius-to-fahrenheit]]))

(def  max-temp-check (r/atom true))
(def min-temp-check (r/atom true))

(defn weather-detail-card [current-data daily-data]
  (let [max-temp (:max (:temp (first daily-data)))
        max-temp-farhenite (.toFixed (celsius-to-fahrenheit max-temp) 2)
        min-temp (:min (:temp (first daily-data)))
        min-temp-farhenite (.toFixed (celsius-to-fahrenheit min-temp) 2)
        sunrise (js/Date. (js/parseInt (str (:sunrise current-data) "000")))
        sunrise-time (date-to-time sunrise)
        sunset (js/Date. (js/parseInt (str (:sunset current-data) "000")))
        sunset-time (date-to-time sunset)]
  [:div {:class "container" }
   [:div {:class "row weather-detail-container"}
    [:div {:class "col col-md-4 col-xl-3"}
     [:div {:class "box weather"}
      [:div {:class "icon bubble black"}
       [:div {:class "spin"}
        [:img {:src "./images/max_temp.png"}]]]
      [:h3 {:class "title"} "Max Temperature"]
      [:h3 {:class "value"} (if (= @max-temp-check true) max-temp max-temp-farhenite)" "
     [:span {:class (if (= @max-temp-check true) "" "transparent-span") :on-click (fn [] (reset! max-temp-check (not @max-temp-check)))} "°C"] " | " 
     [:span {:class (if (= @max-temp-check false) "" "transparent-span") :on-click (fn [] (reset! max-temp-check (not @max-temp-check)))} "°F" 
       ]]]
     ]
    [:div {:class "col col-md-4 col-xl-3"}
     [:div {:class "box weather"}
      [:div {:class "icon bubble black"}
       [:div {:class "spin"}
        [:img {:src "./images/min_temp.png"}]]]
      [:h3 {:class "title"} "Min Temperature"]
      [:h3 {:class "value"} (if (= @min-temp-check true) min-temp min-temp-farhenite)" "
     [:span {:class (if (= @min-temp-check true) "" "transparent-span") :on-click (fn [] (reset! min-temp-check (not @min-temp-check)))} "°C"] " | " 
     [:span {:class (if (= @min-temp-check false) "" "transparent-span") :on-click (fn [] (reset! min-temp-check (not @min-temp-check)))} "°F" 
       ]]]]
    [:div {:class "col col-md-4 col-xl-3"}
     [:div {:class "box weather"}
      [:div {:class "icon bubble black"}
       [:div {:class "spin"}
        [:img {:src "./images/sunrise.png"}]]]
      [:h3 {:class "title"} "Sunrise Time"]
      [:h3 {:class "value"} sunrise-time]]]
    [:div {:class "col col-md-4 col-xl-3"}
     [:div {:class "box weather"}
      [:div {:class "icon bubble black"}
       [:div {:class "spin"}
        [:img {:src "./images/sunset.png"}]]]
      [:h3 {:class "title"} "Sunset Time"]
      [:h3 {:class "value"} sunset-time]]]
    [:div {:class "col col-md-4 col-xl-3"}
     [:div {:class "box weather"}
      [:div {:class "icon bubble black"}
       [:div {:class "spin"}
        [:img {:src "./images/pressure.png"}]]]
      [:h3 {:class "title"} "Pressure"]
      [:h3 {:class "value"} (str (:pressure current-data) " hPa")]]]
    [:div {:class "col col-md-4 col-xl-3"}
     [:div {:class "box weather"}
      [:div {:class "icon bubble black"}
       [:div {:class "spin"}
        [:img {:src "./images/humidity.png"}]]]
      [:h3 {:class "title"} "Humidity"]
      [:h3 {:class "value"} (str (:humidity current-data) "%")]]]
    [:div {:class "col col-md-4 col-xl-3"}
     [:div {:class "box weather"}
      [:div {:class "icon bubble black"}
       [:div {:class "spin"}
        [:img {:src "./images/wind.png"}]]]
      [:h3 {:class "title"} "Wind Speed"]
      [:h3 {:class "value"} (str (:wind_speed current-data) " metre/sec")]]]
    [:div {:class "col col-md-4 col-xl-3"}
     [:div {:class "box weather"}
      [:div {:class "icon bubble black"}
       [:div {:class "spin"}
        [:img {:src "./images/cloud.png"}]]]
      [:h3 {:class "title"} "Cloudiness"]
      [:h3 {:class "value"} (str (:clouds current-data) "%" )]]]]]))

(def temperature-check (r/atom true))

(defn temperature-card [current-data country-name]
  (let [date (js/Date. (js/parseInt (str (:dt current-data) "000")))
        farhenite-temp (.toFixed (celsius-to-fahrenheit  (:temp current-data)) 2)
        weather-condition (:main (first (:weather current-data)))]
  [:div {:class "weather-side"}
   [:div {:class "weather-gradient"}]
   [:div {:class "date-container"}
    [:h2 {:class "date-dayname"} (convert-day (.getDay date))]
    [:span {:class "date-day"}  (.getDate date) " " (convert-month (.getMonth date)) ", " (.getFullYear date)]
    [:span {:class "location"} country-name]]
   [:div {:class "weather-container"}
    [:img {:src (condition-to-image-url weather-condition) :class "weather-icon"}]
    [:h2 {:class "weather-temp"}
     (if (= @temperature-check true) (:temp current-data) farhenite-temp) " "
     [:span {:class (if (= @temperature-check true) "" "transparent-span") :on-click (fn [] (reset! temperature-check (not @temperature-check)))} "°C"] " | "
     [:span {:class (if (= @temperature-check false) "" "transparent-span") :on-click (fn [] (reset! temperature-check (not @temperature-check)))} "°F"]]
    [:h3 {:class "weather-desc"} weather-condition]]]
  ))

(defn weather-card 
  "Generate the weather card given data and country name"
  [current-data daily-data country-name]
  [:div {:class "container weather-card"}
   [:div {:class "row"}
    [:div {:class "col col-md-12 col-lg-3" :style {:padding-right 8} } [temperature-card current-data country-name]]
    [:div {:class "col col-md-12 col-lg-9"} [weather-detail-card current-data daily-data]]]])