(ns dashboard.app
  (:require [reagent.core :as r]
            [reagent.dom :as dom]
            [dashboard.highcharts :as highcharts]
            [reagent-mui.material.button :refer [button]]
            [reagent-mui.material.toggle-button :refer [toggle-button]]
            [reagent-mui.material.toggle-button-group :refer [toggle-button-group]]
            [dashboard.card :refer [weather-card]]
            [reagent-mui.material.text-field :refer [text-field]]
            [ajax.core :refer [json-request-format GET json-response-format]]))

(defonce weather-data (r/atom nil))

(defonce city-name (r/atom ""))

(defonce latitude (r/atom ""))

(defonce longitude (r/atom ""))

(defonce location (r/atom ""))

(defonce search-key (r/atom "Country"))

(defonce flag (r/atom false)) ; true if the user has searched for a city

(defn event-value
  [e]
  (.. e -target -value))

; openweathermap.org API key
(def api-key API_KEY)

(defn get-weather-data [lat long]
  (GET "https://api.openweathermap.org/data/2.5/onecall"
    {:params {"lat" lat
              "lon" long
              "exclude" "minutely,hourly"
              "appid" api-key
              "units" "metric"}
     :format (json-request-format)
     :response-format (json-response-format {:keywords? true})
     :handler (fn [response] (reset! weather-data response))}))

(defn location-to-country [lat long]
  (GET "https://api.openweathermap.org/geo/1.0/reverse"
    {:params {"lat" lat
              "lon" long
              "appid" api-key}
     :format (json-request-format)
     :response-format (json-response-format {:keywords? true})
     :handler (fn [response] (reset! location (str (:name (first response)) "," (:country (first response)) )))}))

(defn country-to-location [country]
  (GET "https://api.openweathermap.org/geo/1.0/direct"
    {:params {"q" country
              "appid" api-key}
     :format (json-request-format)
     :response-format (json-response-format {:keywords? true})
     :handler (fn [response]
                (get-weather-data (:lat (first response)) (:lon (first response))) 
                (reset! location (str (:name (first response)) "," (:country (first response)) )))})
  )

(defn click-handler []
  (do
    (cond
    (= @search-key "Location") (do
                                 (location-to-country @latitude @longitude)
                                 (get-weather-data @latitude @longitude))
    (= @search-key "Country") (country-to-location @city-name)
    )
    (reset! flag true)
    )
  )

(defn Application []
  (let [current-weather-data (:current @weather-data)
        daily-weather-data (:daily @weather-data)]
    [:div
     [:div {:class "container search-container"}
      [:div {:class "row search"}
       [:div {:class "col-lg-6 search-form"}
        (cond (= @search-key "Country") [:input
                                         {:value       @city-name
                                          :class "form-control form-control-lg"
                                          :type "text"
                                          :placeholder "Enter city name"
                                          :aria-label ".form-control-lg example"
                                          :on-change   (fn [e]
                                                         (reset! city-name (event-value e)))}]
              (= @search-key "Location") [:div {:class "row"} [:div {:class "col"}
                                                               [:input
                                                                {:value       @latitude
                                                                 :class "form-control form-control-lg"
                                                                 :type "text"
                                                                 :placeholder "Enter latitude"
                                                                 :aria-label ".form-control-lg example"
                                                                 :on-change   (fn [e]
                                                                                (reset! latitude (event-value e)))}]]
                                          [:div {:class "col"} [:input
                                                                {:value       @longitude
                                                                 :class "form-control form-control-lg"
                                                                 :type "text"
                                                                 :placeholder "Enter country name"
                                                                 :aria-label ".form-control-lg example"
                                                                 :on-change   (fn [e]
                                                                                (reset! longitude (event-value e)))}]]])]
       [:div {:class "col-lg-3 button-div"}
        [button {:variant "contained" :size "medium" :class "button" :on-click #(click-handler)} "Search"]]
       [:div {:class "col-lg-3 toggle-button-group"}
        [toggle-button-group  {:exclusive true
                               :value @search-key
                               :class "toggle-button-group"
                               :on-change (fn [e] (reset! search-key (event-value e)))}
         [toggle-button {:size "large" :class "toggle-button" :value "Country"} "Country"]
         [toggle-button {:size "large" :class "toggle-button" :value "Location"} "Location"]]]]]

     [:div {:class (when (= @flag false) "weather-div")}
      [weather-card current-weather-data daily-weather-data  @location]]
     [:div {:class "container chart-container"}
      [highcharts/render highcharts/weather-forcast-chart daily-weather-data]]]))

(dom/render [Application] (js/document.getElementById "app"))

(defn init []

  (println "App initialization!"))
