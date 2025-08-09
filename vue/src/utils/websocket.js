import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { ElMessage } from 'element-plus';

class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.connected = false;
        this.userId = null;
    }

    connect(userId) {
        this.userId = userId;
        
        const socket = new SockJS('http://localhost:9090/ws', null, {
            transports: ['websocket', 'xhr-streaming', 'xhr-polling']
        });
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, (frame) => {
            console.log('Connected to WebSocket:', frame);
            this.connected = true;
            
            this.stompClient.subscribe(`/user/${userId}/topic/notifications`, (message) => {
                const notification = JSON.parse(message.body);
                this.handleNotification(notification);
            });
            
            this.stompClient.subscribe(`/user/${userId}/topic/orders`, (message) => {
                const update = JSON.parse(message.body);
                this.handleOrderUpdate(update);
            });
            
            if (this.isAdmin()) {
                this.stompClient.subscribe('/topic/admin/orders', (message) => {
                    const adminNotification = JSON.parse(message.body);
                    this.handleAdminNotification(adminNotification);
                });
            }
        }, (error) => {
            console.error('WebSocket connection error:', error);
            this.connected = false;
        });
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.connected = false;
        }
    }

    isAdmin() {
        const user = JSON.parse(localStorage.getItem('canteen-user') || '{}');
        return user.role === 'ADMIN';
    }

    handleNotification(notification) {
        console.log('Received notification:', notification);
        
        if (window.ElMessage) {
            window.ElMessage.info(notification.message);
        }
    }

    handleOrderUpdate(update) {
        console.log('Received order update:', update);
        
        if (update.type === 'ORDER_STATUS_UPDATE') {
            this.updateOrderStatusDisplay(update.orderId, update.status);
            
            if (window.ElMessage) {
                window.ElMessage.success(update.message);
            }
        }
    }

    handleAdminNotification(notification) {
        console.log('Received admin notification:', notification);
        
        if (notification.type === 'NEW_ORDER') {
            try {
                const orderData = JSON.parse(notification.orderData);
                const message = `New order received: ${orderData.content}, Total: ¥${orderData.total}`;
                ElMessage.success(message);
            } catch (e) {
                ElMessage.success('New order received!');
            }
            
            this.refreshOrderList();
        } else if (notification.type === 'ORDER_UPDATE') {
            ElMessage.info(notification.message);
        }
    }

    updateOrderStatusDisplay(orderId, status) {
        const orderElements = document.querySelectorAll(`[data-order-id="${orderId}"]`);
        orderElements.forEach(element => {
            const statusElement = element.querySelector('.order-status');
            if (statusElement) {
                statusElement.textContent = status;
                statusElement.className = `order-status status-${status.toLowerCase()}`;
            }
        });
    }

    refreshOrderList() {
        if (window.refreshOrders) {
            window.refreshOrders();
        } else {
            console.log('Refreshing order list...');
            setTimeout(() => {
                if (window.location.pathname.includes('/manager')) {
                    window.location.reload();
                }
            }, 1000);
        }
    }

    sendMessage(destination, message) {
        if (this.connected && this.stompClient) {
            this.stompClient.send(destination, {}, JSON.stringify(message));
        }
    }
}

export default new WebSocketService(); 