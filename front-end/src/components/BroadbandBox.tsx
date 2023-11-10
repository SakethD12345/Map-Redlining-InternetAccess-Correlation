interface StatusInfo {
  broadband: string | undefined;
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
