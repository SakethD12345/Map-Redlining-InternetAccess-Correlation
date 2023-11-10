interface StatusInfo {
  broadband: string;
}

export default function BroadBandBox({ broadband }: StatusInfo) {
  return (
    <div className="status-form">
      {
        <div>
          <div>BroadBand= {broadband}</div>
        </div>
      }
    </div>
  );
}
