import "./App.css";
import MapBox from "./MapBox";
import InputBox from "./components/SearchKeyword";
import { useState } from "react";
import BroadBandBox from "./components/BroadbandBox";
function App() {
  const [searchOverlay, setSearchOverlay] = useState<
    GeoJSON.FeatureCollection | undefined
  >(undefined);

  const [broadbandPercentage, setBroadbandPercentage] = useState<string | undefined>()
  const [data, setData] = useState<string | undefined>()


  return (
    <div className="App">
      <MapBox data={data} setData={setData}/>
      <div>
        <InputBox data={data} setData={setData}  broadbandPercentage={broadbandPercentage}
                  setBroadbandPercentage={setBroadbandPercentage}/>
        <BroadBandBox broadband={broadbandPercentage} />
      </div>
    </div>
  );
}

export default App;
