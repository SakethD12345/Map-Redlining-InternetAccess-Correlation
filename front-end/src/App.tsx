import "./App.css";
import MapBox from "./MapBox";
import InputBox from "./components/SearchKeyword";
import { useState } from "react";

function App() {
  const [searchOverlay, setSearchOverlay] = useState<
    GeoJSON.FeatureCollection | undefined
  >(undefined);

  const handleSetSearchOverlay = (
    features: GeoJSON.FeatureCollection | undefined
  ) => {
    setSearchOverlay(features);
  };

  return (
    <div className="App">
      <MapBox />
      <div>
        <InputBox setState={handleSetSearchOverlay} />
      </div>
    </div>
  );
}

export default App;
