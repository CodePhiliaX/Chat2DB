const { net } = require('electron');
const { v4: uuidv4 } = require('uuid');
const { machineIdSync } = require('node-machine-id');
const log = require('electron-log');

class Analytics4 {
  constructor(trackingID, secretKey, clientID = machineIdSync(), sessionID = uuidv4()) {
    this.trackingID = trackingID;
    this.secretKey = secretKey;
    this.clientID = clientID;
    this.sessionID = sessionID;
    this.customParams = {};
    this.userProperties = null;
    this.baseURL = 'https://google-analytics.com/mp';
    this.collectURL = '/collect';
  }

  set(key, value) {
    if (value !== null) {
      this.customParams[key] = value;
    } else {
      delete this.customParams[key];
    }
    return this;
  }

  setParams(params) {
    if (typeof params === 'object' && Object.keys(params).length > 0) {
      Object.assign(this.customParams, params);
    } else {
      this.customParams = {};
    }
    return this;
  }

  setUserProperties(upValue) {
    if (typeof upValue === 'object' && Object.keys(upValue).length > 0) {
      this.userProperties = upValue;
    } else {
      this.userProperties = null;
    }
    return this;
  }

  event(eventName) {
    const payload = {
      client_id: this.clientID,
      events: [
        {
          name: eventName,
          params: {
            session_id: this.sessionID,
            ...this.customParams,
          },
        },
      ],
    };

    if (this.userProperties) {
      Object.assign(payload, { user_properties: this.userProperties });
    }

    const url = `${this.baseURL}${this.collectURL}?measurement_id=${this.trackingID}&api_secret=${this.secretKey}`;
    const request = net.request({
      method: 'POST',
      url,
    });

    request.on('response', (response) => {
      let responseData = '';
      response.on('data', (chunk) => {
        responseData += chunk;
      });

      response.on('end', () => {
        if (response.statusCode >= 200 && response.statusCode < 300) {
          log.info('success', responseData);
        } else {
          log.error('response error', response.statusCode);
        }
      });
    });

    request.on('error', (error) => {
      log.error('Error posting data:', error);
    });

    request.write(JSON.stringify(payload));

    request.end();
  }
}

module.exports = Analytics4;
