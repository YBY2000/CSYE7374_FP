import axios from "axios";
import router from "../router";
import {ElMessage} from "element-plus";

// set Axios default URL
axios.defaults.baseURL = import.meta.env.VITE_APP_API_URL;

// Create an Axios instance
const http = axios.create({
    timeout: 5000, headers: {
        'Content-Type': 'application/json'
    }
});

// request interceptors
http.interceptors.request.use(config => {
    if (localStorage.getItem("token")) {
        config.headers["token"] = localStorage.getItem("token");
    }
    return config;
})
;

// response interceptors
http.interceptors.response.use(response => {
    return response.data;
}, error => {
    switch (error.response?.status) {
        case 401:
            localStorage.removeItem("token");
            ElMessage({ message: "Please log in first", type: "error" });
            router.push("/login");
            break;
        case 409:
            ElMessage({ message: error.response.data.data || "Conflict", type: "error" });
            break;
        case 404:
            ElMessage({ message: "API not found", type: "error" });
            break;
        case 500:
            ElMessage({ message: "Internal server error", type: "error" });
            break;
        default:
            ElMessage({ message: error.message || "An unknown error occurred", type: "error" });
            return Promise.reject(error);
        }
    }
);
// environment variables
console.log("ENV:", import.meta.env.NODE_ENV);
console.log("SERVER:", import.meta.env.VUE_APP_SERVER);
console.log("All environment variables:", import.meta.env);
export default http;

