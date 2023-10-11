// cache名,
const cacheName = 'cache';
// cache文件
const cacheFiles = ['/'];

/**
 * 安装 Service Worker
 * install事件是 Service Worker 执行的第一个事件，同一个 Service Worker 只会调用一次
 * 即使 Service Worker 脚本文件只有一个字节不同，浏览器也将视为一个新的 Service Worker
 */
self.addEventListener('install', (e) => {
  e.waitUntil(
    caches.open(cacheName).then((cache) => {
      return cache.addAll(cacheFiles);
    }),
  );
});

/**
 * 激活 Service Worker
 * Service Worker 安装成功之后,会触发activate事件
 * 在这个阶段我们一般做一些清理旧缓存相关的工作
 */
self.addEventListener('activate', (e) => {
  // e.waitUntil(caches.delete(cacheName));
  e.waitUntil(
    caches
      .keys()
      .then((keys) => {
        return Promise.all(
          keys.map((key) => {
            // 清理缓存
            if (cacheName !== key) {
              return caches.delete(key);
            }
          }),
        );
      })
      .then(() => {
        console.log('cache deleted');
      }),
  );
});

self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches
      .open(cacheName)
      .then((cache) => cache.match(event.request, { ignoreSearch: true }))
      .then((response) => {
        return response || fetch(event.request);
      }),
  );
});
