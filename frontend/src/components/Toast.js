import React, { useEffect } from 'react';

const Toast = ({ message, messages, type = 'info', onClose }) => {
  // Captura la propiedad venga como venga y asegura que siempre sea un array
  const data = messages || message || [];
  const safeMessages = Array.isArray(data) ? data : [data];

  useEffect(() => {
    if (!onClose) return;
    const timer = setTimeout(() => onClose(), 3000);
    return () => clearTimeout(timer);
  }, [onClose]);

  if (safeMessages.length === 0) return null;

  return (
    <div className={`toast toast-${type}`}>
      {safeMessages.map((msg, index) => (
        <div key={index} style={{ padding: '8px 12px', marginBottom: '5px' }}>
          {msg}
        </div>
      ))}
    </div>
  );
};

export default Toast;