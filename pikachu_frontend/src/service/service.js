import Axios from 'axios'

export const Service = Axios.create({
    baseURL: "http://localhost:8088"+ "/",
    timeout:10000,
    headers:{
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "GET,POST",
        "Access-Control-Allow-Headers": "*"
    }
})
Service.interceptors.response.use(response => response)