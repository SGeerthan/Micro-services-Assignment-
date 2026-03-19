import React from 'react';
import { 
  PenTool, Highlighter, BookOpen, 
  ShoppingBag, GlassWater, ClipboardList
} from 'lucide-react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

const categories = [
  { name: 'Pens', icon: <PenTool /> },
  { name: 'Pencils', icon: <Highlighter /> },
  { name: 'Notebooks', icon: <BookOpen /> },
  { name: 'School Bags', icon: <ShoppingBag /> },
  { name: 'Drink Bottles', icon: <GlassWater /> },
  { name: 'Stationery Sets', icon: <ClipboardList /> },
];

const LandingPage = () => {
  return (
    <div className="landing-page-wrapper">
      <Navbar />

      <main className="landing-page">
        {/* Hero Banner */}
        <div className="hero-banner">
          <img
            src="/banner.png"
            alt="Back to School Banner"
            className="banner-img"
          />
        </div>

        {/* Categories Grid */}
        <div className="categories-container">
          <h2 className="text-center mb-10 text-3xl font-extrabold" style={{ color: '#111827' }}>
            Shop by Category
          </h2>
          <div 
            className="categories-grid" 
            style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))' }}
          >
            {categories.map((cat, index) => (
              <div key={index} className="category-item cursor-pointer">
                <div className="category-circle transition-all hover:shadow-lg hover:border-primary border border-transparent">
                  {React.cloneElement(cat.icon, { className: "w-10 h-10 text-primary" })}
                </div>
                <span className="category-name transition-colors hover:text-primary">
                  {cat.name}
                </span>
              </div>
            ))}
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default LandingPage;
