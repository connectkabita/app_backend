import React from "react";
import "./ConfirmModal.css"; // add minimal styling

export default function ConfirmModal({ show, message, onConfirm, onCancel }) {
  if (!show) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <p>{message}</p>
        <div className="modal-buttons">
          <button className="btn btn-confirm" onClick={onConfirm}>Yes</button>
          <button className="btn btn-cancel" onClick={onCancel}>No</button>
        </div>
      </div>
    </div>
  );
}
