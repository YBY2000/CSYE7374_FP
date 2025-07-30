import { ElMessage } from 'element-plus'
import router from '../router'
import axios from "axios";

const request = axios.create({
    baseURL: import.meta.env.VITE_BASE_URL,
    timeout: 30000  // Timeout for backend API requests
})


// Request interceptor
// You can handle the request before it is sent
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    return config
}, error => {
    return Promise.reject(error)
});

// Response interceptor
// You can handle the response globally after it is received
request.interceptors.response.use(
    response => {
        let res = response.data;
        // If the response is a file
        if (response.config.responseType === 'blob') {
            return res
        }
        // Handle string data returned from the server
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        return res;
    },
        error => {
        console.log('err' + error)
        return Promise.reject(error)
    }
)


export default request
