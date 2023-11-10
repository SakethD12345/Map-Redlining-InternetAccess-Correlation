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

  const broadband = "";
  const handleSetSearchOverlay = (
    features: GeoJSON.FeatureCollection | undefined
  ) => {
    setSearchOverlay(features);
  };

  return (
    <div className="App">
      <MapBox />
      <div>
        <InputBox setState={handleSetSearchOverlay}  broadbandPercentage={broadbandPercentage}
                  setBroadbandPercentage={setBroadbandPercentage}/>
        <BroadBandBox broadband={broadbandPercentage} />
      </div>
    </div>
  );
}

export default App;
