import axios from 'axios'
import 'element-ui/lib/theme-chalk/index.css';
import { Loading } from 'element-ui';


// // axios请求完成后，隐藏loading
// axios.interceptors.response.use((response) => {
//   Mint.Indicator.close(); // 关闭loading
//   return response
// }, (err) => {
//   return Promise.reject(err)
// })

//1.创建axios实例
const instance = axios.create({
  //2.创建实例时传入axios的配置
  baseURL: "http://127.0.0.1:8080/apis",
  timeout: 50000
})
let loadingInstance;

// 添加请求拦截器
instance.interceptors.request.use(config => {
  loadingInstance  = Loading.service({ //加载loading
    fullscreen: true,
    text: '人工智能思考中...',
    spinner: 'el-icon-loading',
    background: 'rgba(0, 0, 0, 0.7)'
  });
  return config;
}, error => {
  console.log(error);
})

// 添加响应拦截器
instance.interceptors.response.use( (response) => {// 对响应数据做点什么
  loadingInstance.close();//关闭loading
  return response
}, function (error) {// 对响应错误做点什么
  console.log(error);
});

export function request(config){
  //3.发送网络请求, instance返回的是一个promise
  return instance(config);
}



export function request2(config){
  const inst = axios.create({
    //2.创建实例时传入axios的配置
    baseURL: "http://127.0.0.1:8080/apis",
    timeout: 50000
  })
  //3.发送网络请求, instance返回的是一个promise
  return inst(config);
}
