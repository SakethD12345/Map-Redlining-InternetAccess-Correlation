interface StatusInfo {
  broadband: string;
}

export default function BroadBandBox({ broadband }: StatusInfo) {
  return (
    <div className="status-form">
      {
        <div>
          <div>{broadband}</div>
        </div>
      }
    </div>
  );
}
