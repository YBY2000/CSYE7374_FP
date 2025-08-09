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
        console.log('Attempting to connect WebSocket for user:', userId);
        
        const socket = new SockJS('http://localhost:9090/ws', null, {
            transports: ['websocket', 'xhr-streaming', 'xhr-polling']
        });
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, (frame) => {
            console.log('Connected to WebSocket:', frame);
            this.connected = true;
            
            console.log('Setting up subscriptions for user:', userId);
            
            this.stompClient.subscribe(`/user/${userId}/topic/notifications`, (message) => {
                console.log('Received notification message:', message.body);
                const notification = JSON.parse(message.body);
                this.handleNotification(notification);
            });
            
            this.stompClient.subscribe(`/user/${userId}/topic/orders`, (message) => {
                console.log('Received order update message:', message.body);
                const update = JSON.parse(message.body);
                this.handleOrderUpdate(update);
            });
            
            this.stompClient.subscribe(`/user/${userId}/topic/order-status`, (message) => {
                console.log('Received order status change message:', message.body);
                const statusChange = JSON.parse(message.body);
                this.handleOrderStatusChange(statusChange);
            });
            
            if (this.isAdmin()) {
                console.log('Setting up admin subscriptions');
                this.stompClient.subscribe('/topic/admin/orders', (message) => {
                    console.log('Received admin notification message:', message.body);
                    const adminNotification = JSON.parse(message.body);
                    this.handleAdminNotification(adminNotification);
                });
                
                this.stompClient.subscribe('/topic/admin/order-status', (message) => {
                    console.log('Received admin order status change message:', message.body);
                    const adminStatusChange = JSON.parse(message.body);
                    this.handleAdminOrderStatusChange(adminStatusChange);
                });
            }
            
            console.log('All WebSocket subscriptions set up successfully');
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
        this.showUserMessage(notification.message, 'info');
    }

    handleOrderUpdate(update) {
        console.log('Received order update:', update);
        
        if (update.type === 'ORDER_STATUS_UPDATE') {
            this.updateOrderStatusDisplay(update.orderId, update.status);
            this.showUserMessage(update.message, 'success');
        }
    }
    
    handleOrderStatusChange(statusChange) {
        console.log('Received order status change:', statusChange);
        
        const { orderId, orderNo, oldStatus, newStatus, content, total, actionBy } = statusChange;
        
        let message = '';
        let messageType = 'info';
        
        switch (newStatus) {
            case 'CANCELLED':
                message = `Order #${orderNo} has been cancelled`;
                messageType = 'warning';
                break;
            case 'PREPARING':
                message = `Order #${orderNo} has been confirmed and is now being prepared`;
                messageType = 'success';
                break;
            case 'COMPLETED':
                message = `Order #${orderNo} has been completed and is ready for pickup`;
                messageType = 'success';
                break;
            default:
                message = `Order #${orderNo} status changed from ${oldStatus} to ${newStatus}`;
        }
        
        this.showUserMessage(message, messageType);
        this.showDesktopNotification(message, orderNo, newStatus);
        this.updateOrderStatusDisplay(orderId, newStatus);
        this.refreshOrderList();
    }
    
    handleAdminOrderStatusChange(statusChange) {
        console.log('Received admin order status change:', statusChange);
        
        const { orderId, orderNo, userName, oldStatus, newStatus, actionBy } = statusChange;
        
        const message = `Order #${orderNo} (${userName}) status changed from ${oldStatus} to ${newStatus}`;
        
        this.showUserMessage(message, 'info');
        this.showDesktopNotification(message, orderNo, newStatus);
        this.updateOrderStatusDisplay(orderId, newStatus);
        this.refreshOrderList();
    }

    handleAdminNotification(notification) {
        console.log('Received admin notification:', notification);
        
        if (notification.type === 'NEW_ORDER') {
            try {
                const orderData = JSON.parse(notification.orderData);
                const message = `New order received: ${orderData.content}, Total: ¥${orderData.total}`;
                this.showUserMessage(message, 'success');
            } catch (e) {
                this.showUserMessage('New order received!', 'success');
            }
            
            this.refreshOrderList();
        } else if (notification.type === 'ORDER_UPDATE') {
            this.showUserMessage(notification.message, 'info');
        }
    }
    
    showDesktopNotification(message, orderNo, status) {
        if ('Notification' in window && Notification.permission === 'granted') {
            const icon = this.getNotificationIcon(status);
            new Notification(`Canteen Order Update`, {
                body: message,
                icon: icon,
                tag: `order-${orderNo}`,
                requireInteraction: false
            });
        } else if ('Notification' in window && Notification.permission !== 'denied') {
            Notification.requestPermission().then(permission => {
                if (permission === 'granted') {
                    this.showDesktopNotification(message, orderNo, status);
                }
            });
        }
    }
    
    getNotificationIcon(status) {
        switch (status) {
            case 'PREPARING':
                return '/favicon.ico';
            case 'COMPLETED':
                return '/favicon.ico';
            case 'CANCELLED':
                return '/favicon.ico';
            default:
                return '/favicon.ico';
        }
    }

    updateOrderStatusDisplay(orderId, status) {
        const orderRows = document.querySelectorAll(`[data-order-id="${orderId}"]`);
        orderRows.forEach(row => {
            const statusElement = row.querySelector('.order-status');
            if (statusElement) {
                statusElement.textContent = status;
                statusElement.className = `order-status status-${status.toLowerCase()}`;
            }
        });
        
        const tagElements = document.querySelectorAll('.el-tag');
        tagElements.forEach(tag => {
            if (tag.textContent.trim() === status) {
                tag.className = this.getTagClass(status);
            }
        });
    }
    
    getTagClass(status) {
        switch (status) {
            case 'PENDING':
                return 'el-tag el-tag--warning';
            case 'PREPARING':
                return 'el-tag el-tag--primary';
            case 'COMPLETED':
                return 'el-tag el-tag--success';
            case 'CANCELLED':
                return 'el-tag el-tag--danger';
            default:
                return 'el-tag el-tag--info';
        }
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
    
    showUserMessage(message, type = 'info') {
        console.log(`[${type.toUpperCase()}] ${message}`);
        
        // 尝试使用全局 ElMessage
        if (window.ElMessage) {
            try {
                window.ElMessage[type](message);
                return;
            } catch (e) {
                console.warn('Failed to use window.ElMessage:', e);
            }
        }
        
        // 尝试使用导入的 ElMessage
        try {
            ElMessage[type](message);
            return;
        } catch (e) {
            console.warn('Failed to use imported ElMessage:', e);
        }
        
        // 降级到原生 alert
        alert(`${type.toUpperCase()}: ${message}`);
    }
}

export default new WebSocketService(); 